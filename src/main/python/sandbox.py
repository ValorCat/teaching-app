import builtins
import io
import sys

builtin_blacklist = ['breakpoint', 'copyright', 'compile', 'exit', 'license', 'memoryview', 'quit']

module_whitelist = ['calendar', 'cmath', 'collections', 'copy', 'dataclasses', 'datetime', 'decimal', 'difflib',
                    'fractions', 'functools', 'itertools', 'math', 'numbers', 'operator', 'random', 're', 'statistics',
                    'string', 'stringprep', 'textwrap', 'typing', 'unicodedata']

module_exports = {
    'datetime': ['date', 'datetime', 'MAXYEAR', 'MINYEAR', 'time', 'timedelta', 'timezone', 'tzinfo']
}

current_sandbox = None


class Sandbox:
    def __init__(self, stdin: str, files: dict):
        self.stdin = io.StringIO(stdin)
        self.stdout = io.StringIO()
        self.stderr = self.stdout
        self.file_system = files.copy()
        self.file_handles = []
        self.vars = self._setup_env()
        self.exception = None

    @staticmethod
    def _setup_env():
        new_builtins = builtins.__dict__.copy()
        for name in builtin_blacklist:
            del new_builtins[name]
        new_builtins['open'] = new_open
        new_builtins['__import__'] = new_import
        return {
            '__name__': '__main__',
            '__builtins__': new_builtins
        }

    def __enter__(self) -> "Sandbox":
        global current_sandbox
        sys.stdin = self.stdin
        sys.stdout = self.stdout
        sys.stderr = self.stderr
        current_sandbox = self
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        global current_sandbox
        current_sandbox = None
        sys.stdin = sys.__stdin__
        sys.stdout = sys.__stdout__
        sys.stderr = sys.__stderr__
        for handle in self.file_handles:
            handle.close()
        if exc_type is not None:
            self.exception = exc_val.with_traceback(exc_tb)
        return True  # suppress exception


class IOWrapper(io.StringIO):
    def __init__(self, file: str, readonly : bool = False):
        super().__init__(current_sandbox.file_system[file])
        self._name = file
        self._readonly = readonly
        current_sandbox.file_handles.append(self)

    def close(self) -> None:
        current_sandbox.file_handles.remove(self)
        super().close()

    def flush(self) -> None:
        if self.writable():
            current_sandbox.file_system[self._name] = super().getvalue()

    def getvalue(self) -> str:
        raise io.UnsupportedOperation()

    def writable(self) -> bool:
        return not self._readonly

    def write(self, s: str) -> int:
        if not self.writable():
            raise io.UnsupportedOperation('not writable')
        self.flush()
        return super().write(s)

    def writelines(self, lines) -> None:
        for line in lines:
            self.write(line)


def new_open(file, mode='r'):
    """
    Open file and return a stream. Raises OSError upon failure.

    file is string giving the name (and the path if the file isn't in the
    current working directory) of the file to be opened.

    mode is an optional string that specifies the mode in which the file
    is opened. It defaults to 'r' which means open for reading in text
    mode.  Other common values are 'w' for writing (truncating the file if
    it already exists), 'x' for creating and writing to a new file, and
    'a' for appending (which on some Unix systems, means that all writes
    append to the end of the file regardless of the current seek position).
    The available modes are:

    ========= ===============================================================
    Character Meaning
    --------- ---------------------------------------------------------------
    'r'       open for reading (default)
    'w'       open for writing, truncating the file first
    'x'       create a new file and open it for writing
    'a'       open for writing, appending to the end of the file if it exists
    ========= ===============================================================
    """
    if type(mode) != str:
        raise TypeError('open() argument 2 must be str, not ' + type(mode).__name__)
    if mode not in ['r', 'w', 'a', 'x']:
        raise ValueError('invalid mode: ' + repr(mode))
    if type(file) != str:
        raise OSError('the handle is invalid')
    if mode == 'r':
        if file not in current_sandbox.file_system:
            raise FileNotFoundError('no such file or directory: ' + repr(file))
        return IOWrapper(file, readonly=True)
    elif mode == 'w':
        current_sandbox.file_system[file] = ''
        return IOWrapper(file)
    elif mode == 'a':
        handle = IOWrapper(file)
        handle.seek(0, io.SEEK_END)
        return handle
    elif mode == 'x':
        if file in current_sandbox.file_system:
            raise FileExistsError('file exists: ' + repr(file))
        return IOWrapper(file)


def new_import(name, global_dict=None, local_dict=None, fromlist=(), level=0):
    """
    Import a module. This function is meant for use by the
    Python interpreter.
    """
    if '.' in name:
        if name[0] == '.':
            name = '__main__' + name
        package = name.split('.')[0]
        raise ModuleNotFoundError(f"No module named '{name}'; '{package}' is not a package")
    elif name not in module_whitelist:
        raise ModuleNotFoundError(f"No module named '{name}'")
    module = builtins.__import__(name, global_dict, local_dict, fromlist, level)
    for attr in fromlist or ():
        if not is_exported(module, attr):
            raise ImportError(f"cannot import name '{attr}' from '{name}'")
    if hasattr(module, '__loader__'):
        del module.__loader__
    module.__getattribute__ = module_access
    return module


def is_exported(module, attr: str):
    env = module.__dict__
    name = module.__name__
    exports = env.get('__all__') or module_exports.get(name)
    return not exports or attr in exports


def module_access(module, attr: str):
    if not is_exported(module, attr):
        raise AttributeError(f"module '{module.__name__}' has no attribute '{attr}'")
    return module.__dict__[attr]

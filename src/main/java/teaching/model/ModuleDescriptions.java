package teaching.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ModuleDescriptions {

    public static final Map<String, String> MODULES = new HashMap<>();

    public static List<Map.Entry<String, String>> get() {
        return MODULES.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toList());
    }

    static {
        MODULES.put("calendar", "General calendar-related functions");
        MODULES.put("cmath", "Mathematical functions for complex numbers");
        MODULES.put("collections", "Container datatypes");
        MODULES.put("copy", "Shallow and deep copy operations");
        MODULES.put("dataclasses", "Data Classes");
        MODULES.put("datetime", "Basic date and time types");
        MODULES.put("decimal", "Decimal fixed point and floating point arithmetic");
        MODULES.put("difflib", "Helpers for computing deltas");
        MODULES.put("fractions", "Rational numbers");
        MODULES.put("functools", "Higher-order functions and operations on callable objects");
        MODULES.put("itertools", "Functions creating iterators for efficient looping");
        MODULES.put("math", "Mathematical functions");
        MODULES.put("numbers", "Numeric abstract base classes");
        MODULES.put("operator", "Standard operators as functions");
        MODULES.put("random", "Generate pseudo-random numbers");
        MODULES.put("re", "Regular expression operations");
        MODULES.put("statistics", "Mathematical statistics functions");
        MODULES.put("string", "Common string operations");
        MODULES.put("stringprep", "Internet String Preparation");
        MODULES.put("textwrap", "Text wrapping and filling");
        MODULES.put("typing", "Support for type hints");
        MODULES.put("unicodedata", "Unicode Database");
    }

}

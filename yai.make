BUILD_DIR = build

SOURCES := $(wildcard com/interpreter/$(PACKAGE)/*.java)
CLASSES := $(addprefix $(BUILD_DIR)/, $(SOURCES:.java=.class))

JAVA_OPTIONS := -Werror

default: $(CLASSES)
	@: # Don't show "Nothing to be done" output.

$(BUILD_DIR)/%.class: %.java
	@ javac -cp . -d $(BUILD_DIR) $(JAVA_OPTIONS) -implicit:none $<
	@ printf "%8s %-60s %s\n" javac $< "$(JAVA_OPTIONS)"

.PHONY: default
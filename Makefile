BUILD_DIR = build
PYTHON = python3
SOURCES := $(wildcard com/interpreter/yai/*.java)
CLASSES := $(addprefix $(BUILD_DIR)/, $(SOURCES:.java=.class))

JAVA_OPTIONS := -Werror

yai: $(CLASSES)
	@: # Don't show "Nothing to be done" output.

$(BUILD_DIR)/%.class: %.java
	@ javac -cp . -d $(BUILD_DIR) $(JAVA_OPTIONS) -implicit:none $<
	@ printf "%8s %-60s %s\n" javac $< "$(JAVA_OPTIONS)"

run: yai
	cd build; java com.interpreter.yai.Yai

generate_ast:
	$(PYTHON) generate_ast.py com/interpreter/yai

clean:
	rm -rf $(BUILD_DIR)/*

.PHONY: default

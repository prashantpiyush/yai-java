BUILD_DIR = build

SOURCES := $(wildcard com/interpreter/$(PACKAGE)/*.java)
CLASSES := $(addprefix $(BUILD_DIR)/, $(SOURCES:.java=.class))

yai: $(CLASSES)
	@ $(MAKE) -f yai.make PACKAGE=yai

run: yai
	cd build; java com.interpreter.yai.Yai

clean:
	rm -rf $(BUILD_DIR)/*

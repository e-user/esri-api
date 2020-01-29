SOURCES := $(shell find src -name \*.clj)
MAIN_CLASS = esri_api.core
DEP_CLASSES = com.stuartsierra.component

help: ## Show help
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'

resources:
	@$(MAKE) -C $@ init

classes:
	@mkdir -p $@

$(MAIN_CLASS): classes $(SOURCES)
	@clj -e "(compile '$@)"

$(DEP_CLASSES): classes
	@clj -e "(compile '$@)"

aot-compile: $(MAIN_CLASS) $(DEP_CLASSES) ## Perform ahead-of-time compilation

target/esri-api.jar: resources aot-compile
	@clj -Sdeps '{:deps {uberdeps {:mvn/version "0.1.8"}}}' -m uberdeps.uberjar --main-class $(MAIN_CLASS)

uberjar: target/esri-api.jar ## Build uberjar file

test: ## Run all tests
	@clojure -A:test -m esri_api.test

clean: ## Remove generated files
	@rm -rf classes target

.PHONY: resources init aot-compile uberjar clean classes $(MAIN_CLASS) $(DEP_CLASSES) test

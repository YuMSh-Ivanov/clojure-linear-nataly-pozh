#!/bin/bash

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
java --class-path "$SCRIPT_DIR/lib/*" clojure.main "$@"

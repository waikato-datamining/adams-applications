#!/bin/bash

`dirname $0`/terminal.sh -memory 512m -main adams.terminal.Main -title ADAMS-Incubator -env-modifier adams.core.management.WekaHomeEnvironmentModifier

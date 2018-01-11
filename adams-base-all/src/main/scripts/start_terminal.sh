#!/bin/bash

`dirname $0`/terminal.sh -memory 512m -main adams.terminal.Main -memory 512m -title ADAMS-Base -env-modifier adams.core.management.WekaHomeEnvironmentModifier

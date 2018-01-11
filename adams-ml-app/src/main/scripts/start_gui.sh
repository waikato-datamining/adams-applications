#!/bin/bash

`dirname $0`/launcher.sh -main adams.gui.Main -memory 2g -title ADAMS-ML -env-modifier adams.core.management.WekaHomeEnvironmentModifier

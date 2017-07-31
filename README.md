[![Build Status](https://travis-ci.org/broadinstitute/workbench-libs.svg?branch=develop)](https://travis-ci.org/broadinstitute/workbench-libs)

# workbench-libs
Workbench utility libraries, built for Scala 2.11 and 2.12. You can find the full list of packages at [Artifactory](https://broadinstitute.jfrog.io/broadinstitute/webapp/#/artifacts/browse/tree/General/libs-release-local/org/broadinstitute/dsde/workbench/).

In this repo:

## workbench-utils

Contains utility functions and classes.

Latest SBT dependency: `"org.broadinstitute.dsde.workbench" %% "workbench-util" % "0.2-f87e766"`

[Changelog](util/CHANGELOG.md)

#### Contents

- `Retry`, containing logic to deal with retrying `Future`s, including exponential backoff behavior
- `FutureSupport`, containing some useful utilities for working with `Future`s
- `MockitoTestUtils.captor`, some Scala sugar for Mockito's `ArgumentCaptor`
    - To use this, additionally depend on `("org.broadinstitute.dsde.workbench" %% "workbench-util" % "0.x-githash" % Test).classifier("tests")`

## workbench-model

Contains generic, externally-facing model classes used across Workbench.

Latest SBT dependency: `"org.broadinstitute.dsde.workbench" %% "workbench-model" % "0.1-f87e766"`

[Changelog](model/CHANGELOG.md)

NOTE: This library uses akka-http's implementation of spray-json and is therefore not compatible with spray, which you shouldn't be using anyway because it is no longer being maintained.

#### Contents

- `ErrorReport`

## workbench-metrics

Contains utilities for instrumenting Scala code and reporting to StatsD using [metrics-scala](https://github.com/erikvanoosten/metrics-scala) and [metrics-statsd](https://github.com/ReadyTalk/metrics-statsd).

Latest SBT dependency: `"org.broadinstitute.dsde.workbench" %% "workbench-metrics" % "0.1-xxxxxxx"`

[Changelog](metrics/CHANGELOG.md)

## workbench-google

Coming soon!

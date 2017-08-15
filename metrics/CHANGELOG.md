# Changelog

This file documents changes to the `workbench-metrics` library, including notes on how to upgrade to new versions.

## 0.2

### Added

- Added `ExpandedMetricBuilder.asGaugeIfAbsent` method to handle gauge name conflicts.
- Added InstrumentedRetry trait which updates a histogram with the number of errors in a `RetryableFuture`.

### Changed 

- Updated method signature of `org.broadinstitute.dsde.workbench.metrics.startStatsDReporter` to take an API key

## 0.1

### Added

- This library
- `WorkbenchInstrumented`, a mixin trait for instrumenting arbitrary code
- `InstrumentationDirectives.instrumentRequest`, an akka-http directive for instrumenting incoming HTTP requests
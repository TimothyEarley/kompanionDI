# Speed comparison (stolen from Katana)

Implementation of Kompanion for the speed comparison done by [katana](https://github.com/rewe-digital-incubator/katana/tree/master/speed-comparison).

The only change is the addition of the KompanionDI measurement.

## Library versions

| Library   | Version |
|-----------|---------|
| Katana    | 1.0.1   |
| Koin      | 1.0.1   |
| Kodein    | 5.3.0   |
| Kompanion | 0.4.0   |

## Results

All times in nanoseconds.

| Library   | Setup (average) | Setup (median) | Execution (average) | Execution (median) |
| --------- | ---------------:| --------------:| -------------------:| ------------------:|
| Kompanion |       79.589999 |           56.0 |          174.169066 |              129.0 |
| Katana    |      749.253171 |          568.0 |          310.425195 |              239.0 |
| Kodein    |     1246.768735 |          861.0 |         1185.986315 |              607.0 |
| Koin      |     3727.040066 |         2847.0 |         6738.607349 |             5501.0 |

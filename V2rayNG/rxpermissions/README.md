# rxpermissions (vendored)

RxPermissions 0.12 (tbruyelle/RxPermissions, tag v0.12, Apache-2.0),
vendored as a local module because JitPack cannot build it: the library's
own build uses the dead Bintray plugin (`gradle-bintray-plugin:1.8.0`,
JCenter shut down), so `com.github.tbruyelle:rxpermissions:0.12` is
unresolvable from JitPack and the CI build failed on it (2026-11-08, run #2).

Only the 3 library sources are vendored (package
`com.tbruyelle.rxpermissions3`, unchanged, so all app call sites work
unmodified). No sample, no tests, no Bintray/JitPack build files.
Upstream Apache-2.0 LICENSE is copied here as LICENSE.

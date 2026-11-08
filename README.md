# Spider Proxy Finder

A free-proxy VPN client for Android — find working free VLESS / VMess / Trojan /
Shadowsocks / Hysteria2 servers and connect with one tap.

Based on [NikaNG](https://github.com/mahsanet/NikaNG) (a v2rayNG fork with
Mahsa Xray-core) and [v2rayNG](https://github.com/2dust/v2rayNG), licensed
GPL-3.0. See [LICENSE](LICENSE) and [CR.md](CR.md).

## What it does

- Ships with 4 free-config feeds built in (Pawdroid, getpower668, nodesradar,
  CyberShield) — no manual subscription links needed.
- Reality-only filter on the 3 GitHub feeds (only VLESS Reality configs are
  imported); the CyberShield feed is imported with all protocols.
- After every subscription update it runs the real Xray URL test on each
  config and **auto-removes dead ones** — the list only ever shows servers
  that actually connect.
- Auto-update + auto-test every 3 hours in the background.
- Default routing: bypass LAN / private addresses only, proxy everything else.
- No analytics, no config-feedback reporting, no ads.

## Build

Built by GitHub Actions (`.github/workflows/build.yml`): the Mahsa
Xray-core AAR is built from source (GFW-knocker/AndroidLibXrayLite +
GFW-knocker/Xray-core, gomobile bind), then Gradle assembles the release APK.

## License

GPL-3.0 — see [LICENSE](LICENSE).

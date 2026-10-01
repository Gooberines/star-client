
<p align="center">
<img src="src/main/resources/assets/meteor-client/icon.png" alt="star-client-logo" width="15%"/>
</p>

<h1 align="center">Star Client</h1>
<p align="center">A Minecraft Fabric Utility Mod for anarchy servers. (Minecraft 1.21.11)</p>

<p align="center"><i>Star Client is a rebrand of <a href="https://github.com/MeteorDevelopment/meteor-client">Meteor Client</a>, distributed under the GNU General Public License v3.0. Internally it keeps Meteor's mod id and API so that Meteor addons remain compatible.</i></p>

## Bundled addons
Star Client ships with these Meteor addons included (jar-in-jar):

- [Meteor Rejects](https://github.com/AntiCope/meteor-rejects)
- [Meteorist](https://github.com/zgoly/Meteorist)
- [Numby Hack](https://github.com/cqb13/Numby-Hack)
- [Trouser Streak](https://github.com/pwnoobs/trouser-streak)
- PowHax
- Dino Printer
- Meteor Extras
- Nora Tweaks
- Meteor+
- Zinc / SweetMods

## Usage

### Building
- Clone this repository (requires JDK 21)
- Build the client: `./gradlew build`
- Bundle the addons into the jar: `python scripts/bundle_addons.py`
- The finished jar is `build/libs/star-client-<mcversion>.jar`

### Installation
Put the built jar from `build/libs` into your Fabric `mods` folder for Minecraft 1.21.11 (Fabric API required).

## Credits
- **Star** — Star Client (this rebrand)
- **[MineGame159](https://github.com/MineGame159)** and Meteor Development — [Meteor Client](https://github.com/MeteorDevelopment/meteor-client), the original base this project is built on
- The authors of the bundled addons listed above
- [Cabaletta](https://github.com/cabaletta) and [WagYourTail](https://github.com/wagyourtail) for [Baritone](https://github.com/cabaletta/baritone)
- The [Fabric Team](https://github.com/FabricMC) for [Fabric](https://github.com/FabricMC/fabric-loader) and [Yarn](https://github.com/FabricMC/yarn)

## Licensing
This project is licensed under the [GNU General Public License v3.0](https://www.gnu.org/licenses/gpl-3.0.en.html).

If you use **ANY** code from the source:
- You must disclose the source code of your modified work and the source code you took from this project.
- You must state clearly and obviously to all end users that you are using code from this project.
- Your application must also be licensed under the same license.

Bundled addons remain under their respective licenses and copyrights.

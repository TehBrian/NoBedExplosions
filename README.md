# NoBedExplosions
Control bed and respawn anchor functionality across all of your worlds, such as
allowing sleep in the nether or the end!

* [Download](https://modrinth.com/plugin/nobedexplosions)
* [Discord](https://thbn.me/discord)
* [Donate](https://github.com/sponsors/TehBrian) <3

---

## Features
This is a simple yet customizable plugin that provides per-world configuration
for beds. With it, you can:

- allow sleeping in the nether or end, setting the player's home
- disable entering beds, thereby preventing explosions
- explode beds in the overworld

Because it has multi-world support, you can mix and match functionalities and
worlds to your heart's content!

It can also prevent respawn anchor explosions too.

## Commands and Permissions
`/nbe info [world]` permission: `nobedexplosions.info`

`/nbe reload` permission: `nobedexplosions.reload`

## Configs
[`config.yml`](https://github.com/TehBrian/NoBedExplosions/blob/main/src/main/resources/config.yml)

[`worlds.yml`](https://github.com/TehBrian/NoBedExplosions/blob/main/src/main/resources/worlds.yml)

[`lang.yml`](https://github.com/TehBrian/NoBedExplosions/blob/main/src/main/resources/lang.yml)

## Building
This project uses Gradle. To build, run `./gradlew build` in the project's root
directory. The built jar can be found in `build/libs`.

## Contributing
Feel free to submit a pull request or file an issue! All changes are welcome. If
you're contributing code, please follow the project's code style.

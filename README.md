BhopPlugin
A lightweight bunny hop plugin for Paper servers that preserves and amplifies player momentum across jumps.
Features

Smooth momentum accumulation on consecutive jumps
Configurable speed boost and speed cap
Permission-based access control
Momentum resets on water contact or standing still for too long
No external dependencies

Requirements

Paper 1.21+
Java 21+

Installation

Download the latest .jar from the releases page
Drop it into your server's plugins/ folder
Restart the server
Configure plugins/BhopPlugin/config.yml to your liking

Configuration
yamlbhop:
  # Multiplier applied to the player's speed on each jump.
  # Higher values = faster acceleration. Recommended: 1.1 - 1.5
  speed-boost: 1.28

  # Maximum horizontal speed a player can reach (in blocks/tick).
  # Vanilla sprint speed is roughly 0.28. Default cap is ~4.5x that.
  max-speed: 4.5

  # Enable debug logging in console (for development only).
  debug: false
Permissions
PermissionDescriptionDefaultbhop.useAllows the player to use bhopfalsebhop.reloadAllows reloading the configop
To give all players access via LuckPerms:
/lp group default permission set bhop.use true
Commands
CommandDescriptionPermission/bhopreloadReloads the plugin configbhop.reload
How it works
When a player jumps, their current stored speed is multiplied by speed-boost and capped at max-speed. This new speed is then applied to their horizontal velocity, preserving direction. Momentum is lost if the player stands on the ground for more than 500ms or enters water.
License
MIT

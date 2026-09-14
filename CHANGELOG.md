# Changelog

## 4.0.1

- An item transfer that is rolled back no longer rebuilds the slot it touched from a single stack,
  so an inventory holding more than a stack in a slot keeps everything in it.
- On Fabric, a block whose slot count changes while it runs is no longer frozen at the count it had
  when another mod first looked it up.

## 4.0.0

Updated to Minecraft 26.2, for NeoForge and Fabric.

- Rebuilt on the 26.2 registry, model, networking and inventory APIs.

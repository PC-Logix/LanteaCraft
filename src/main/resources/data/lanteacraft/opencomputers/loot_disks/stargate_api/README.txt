LanteaCraft Stargate API

Copy lib/stargate.lua to /lib/stargate.lua on the target OpenComputers computer,
then load it with:

  local stargate = require("stargate")

The wrapper uses the primary stargate component by default. Pass a component
address as the final argument to select another gate.

The disk also contains a real dialing program at /bin/stargate:

  stargate <address>
  stargate status
  stargate disconnect
  stargate iris open

The disk also contains a real dialing program at /bin/stargate:

  stargate <address>
  stargate status
  stargate disconnect
  stargate iris open

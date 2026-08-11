local component = require("component")
local event = require("event")

local stargate = {}

local function resolve(address)
  if type(address) == "table" then
    return address
  end

  address = address or component.getPrimary("stargate")
  if not address then
    error("no stargate component found", 3)
  end

  local proxy = component.proxy(address)
  if not proxy then
    error("no stargate component at " .. tostring(address), 3)
  end
  return proxy
end

local function invoke(address, method, ...)
  return resolve(address)[method](...)
end

-- Return the raw component proxy. The optional address may also be a proxy.
function stargate.component(address)
  return resolve(address)
end

function stargate.getAddress(address)
  return invoke(address, "getAddress")
end

function stargate.getStatus(address)
  return invoke(address, "getStatus")
end

function stargate.isConnected(address)
  return invoke(address, "isConnected")
end

function stargate.isDialing(address)
  return invoke(address, "isDialing")
end

function stargate.getRemoteAddress(address)
  return invoke(address, "getRemoteAddress")
end

-- The gate component address is optional and is the final argument.
function stargate.dial(destination, address)
  return invoke(address, "dial", destination)
end

function stargate.disconnect(address)
  return invoke(address, "disconnect")
end

function stargate.hasIris(address)
  return invoke(address, "hasIris")
end

function stargate.getIrisType(address)
  return invoke(address, "getIrisType")
end

function stargate.getIrisState(address)
  return invoke(address, "getIrisState")
end

function stargate.openIris(address)
  return invoke(address, "openIris")
end

function stargate.closeIris(address)
  return invoke(address, "closeIris")
end

function stargate.isIrisRedstoneEnabled(address)
  return invoke(address, "isIrisRedstoneEnabled")
end

function stargate.isIrisRedstoneLocked(address)
  return invoke(address, "isIrisRedstoneLocked")
end

function stargate.setIrisRedstoneEnabled(enabled, address)
  return invoke(address, "setIrisRedstoneEnabled", enabled)
end

function stargate.getGdoCode(address)
  return invoke(address, "getGdoCode")
end

function stargate.setGdoCode(code, address)
  return invoke(address, "setGdoCode", code)
end

function stargate.pullEvent(timeout)
  if timeout == nil then
    return event.pull("stargate_event")
  end
  return event.pull(timeout, "stargate_event")
end

function stargate.pullIdcEvent(timeout)
  if timeout == nil then
    return event.pull("stargate_idc_received")
  end
  return event.pull(timeout, "stargate_idc_received")
end

return stargate

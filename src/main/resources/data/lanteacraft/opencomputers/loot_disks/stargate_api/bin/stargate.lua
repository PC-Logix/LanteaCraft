local shell = require("shell")
local stargate = require("stargate")

local args, options = shell.parse(...)
local componentAddress = options.a
local command = args[1]

if not command or command == "help" or options.h then
  io.write("Usage:\n")
  io.write("  stargate <address> [-a <component>]\n")
  io.write("  stargate status [-a <component>]\n")
  io.write("  stargate disconnect [-a <component>]\n")
  io.write("  stargate iris open|close [-a <component>]\n")
  return command and 0 or 1
end

local function call(method, ...)
  local ok, result = pcall(stargate[method], ..., componentAddress)
  if not ok then
    io.stderr:write(tostring(result) .. "\n")
    return nil, 1
  end
  return result, 0
end

if command == "status" then
  local status, code = call("getStatus")
  if not status then
    return code
  end
  io.write("address: " .. tostring(status.address) .. "\n")
  io.write("connected: " .. tostring(status.connected) .. "\n")
  io.write("dialing: " .. tostring(status.dialing) .. "\n")
  io.write("remote: " .. tostring(status.remoteAddress) .. "\n")
  io.write("iris: " .. tostring(status.irisState) .. "\n")
  return 0
elseif command == "disconnect" then
  local result, code = call("disconnect")
  if not result then
    return code
  end
  io.write(tostring(result.message) .. "\n")
  return result.success and 0 or 1
elseif command == "iris" then
  local action = args[2]
  local method = action == "open" and "openIris" or action == "close" and "closeIris"
  if not method then
    io.stderr:write("usage: stargate iris open|close [-a <component>]\n")
    return 1
  end
  local result, code = call(method)
  if not result then
    return code
  end
  return result and 0 or 1
end

local destination = command == "dial" and args[2] or command
if not destination or destination == "" then
  io.stderr:write("missing destination address\n")
  return 1
end

local result, code = call("dial", destination)
if not result then
  return code
end
io.write(tostring(result.message) .. "\n")
return result.success and 0 or 1

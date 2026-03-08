local key = KEYS[1]
local limit = tonumber(ARGV[1])
local timeout = tonumber(ARGV[2])

local current = redis.call('get', key)

if current == false then
    redis.call('set', key, 1, 'EX', timeout)
    return 1
elseif tonumber(current) < limit then
    redis.call('incr', key)
    return 1
else
    return 0
end

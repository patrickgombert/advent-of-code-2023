def parse_game_results(line)
  game_header, raw_results = line.split(':')
  game_id = game_header.split(' ')[1].to_i

  [game_id, _parse_raw_results(raw_results)]
end

def _parse_raw_results(raw_results)
  raw_results.split(';').map do |draw|
    draw.strip.split(',').reduce({}) do |acc, pull|
      count_str, type = pull.split(' ')
      acc[type] = count_str.to_i
      acc
    end
  end
end

def valid?(results)
  results.all? do |result|
    result.fetch('red', 0) <= 12 &&
    result.fetch('green', 0) <= 13 &&
    result.fetch('blue', 0) <= 14
  end
end

puts File.readlines('input')
  .map { |line| parse_game_results(line.strip) }
  .filter { |(_, results)| valid?(results) }
  .map { |(game_id, _)| game_id }
  .sum

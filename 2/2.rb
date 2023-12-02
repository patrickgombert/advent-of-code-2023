require_relative '1'

def minimum_cubes(results)
  results.reduce({}) do |min_cubes, result|
    result.keys.each do |color|
      if min_cubes.fetch(color, 0) < result[color]
        min_cubes[color] = result[color]
      end
    end
    min_cubes
  end
end

puts File.readlines('input')
  .map { |line| parse_game_results(line.strip) }
  .map { |(_, results)| minimum_cubes(results) }
  .map { |min_cubes| min_cubes.values.reduce(:*) }
  .sum

require_relative '1'

class DynamicExpandingUniverse < Universe
  def expand!(expansion_distance)
    empty_rows = (0..@bound.x).filter do |row|
      !@galaxies.values.any? { |galaxy| galaxy.x == row }
    end
    empty_cols = (0..@bound.y).filter do |col|
      !@galaxies.values.any? { |galaxy| galaxy.y == col }
    end

    @galaxies = @galaxies.reduce({}) do |acc, (k, v)|
      shifted_xs = empty_rows.filter do |row|
        row < v.x
      end.size * (expansion_distance - 1)
      shifted_ys = empty_cols.filter do |col|
        col < v.y
      end.size * (expansion_distance - 1)

      acc[k] = Position.new(v.x + shifted_xs, v.y + shifted_ys)
      acc
    end
  end
end

universe = DynamicExpandingUniverse.new('input')
universe.parse!
universe.expand!(1_000_000)
puts universe.distances

class Position
  attr_reader :x, :y

  def initialize(x, y)
    @x = x
    @y = y
  end

  def distance(other)
    (@x - other.x).abs + (@y - other.y).abs
  end

  def eql?(other)
    @x == other.x && @y == other.y
  end
  alias :== :eql?

  def hash
    -@x.hash ^ @y.hash
  end

  def to_s
    "(#{@x}, #{@y})"
  end
end

class Universe
  attr_reader :galaxies

  def initialize(file_path)
    @file_path = file_path
    @galaxies = {}
    @bound = Position.new(0, 0)
  end

  def parse!
    counter = 1
    File.readlines(@file_path).each_with_index do |row, x|
      row.strip.split('').each_with_index do |char, y|
        position = Position.new(x, y)
        if char == '#'
          @galaxies[counter] = position
          counter = counter + 1
        end
        maybe_set_bound(position)
      end
    end
  end

  def expand!
    empty_rows = (0..@bound.x).filter do |row|
      !@galaxies.values.any? { |galaxy| galaxy.x == row }
    end
    empty_cols = (0..@bound.y).filter do |col|
      !@galaxies.values.any? { |galaxy| galaxy.y == col }
    end

    @galaxies = @galaxies.reduce({}) do |acc, (k, v)|
      shifted_xs = empty_rows.filter do |row|
        row < v.x
      end.size
      shifted_ys = empty_cols.filter do |col|
        col < v.y
      end.size
      acc[k] = Position.new(v.x + shifted_xs, v.y + shifted_ys)
      acc
    end
  end

  def distances
    pairs = @galaxies.keys.flat_map do |galaxy_a|
      @galaxies.keys.map do |galaxy_b|
        if galaxy_a != galaxy_b
          [galaxy_a, galaxy_b].sort
        end
      end.filter { |pair| !pair.nil? }
    end.uniq

    pairs.map do |pair|
      @galaxies[pair[0]].distance(@galaxies[pair[1]])
    end.sum
  end

  private

  def maybe_set_bound(position)
    if position.x > @bound.x
      @bound = Position.new(position.x, @bound.y)
    end
    if position.y > @bound.y
      @bound = Position.new(@bound.x, position.y)
    end
  end
end

universe = Universe.new('input')
universe.parse!
universe.expand!
puts universe.distances

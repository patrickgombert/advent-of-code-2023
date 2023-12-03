require 'set'

class Point
  attr_reader :x, :y

  def initialize(x, y)
    @x = x
    @y = y
  end

  def candidate_neighbors
    [-1, 0, 1].flat_map do |x_offset|
      [-1, 0, 1].map do |y_offset|
        Point.new(@x + x_offset, @y + y_offset)
      end
    end.filter do |candidate|
      !(candidate.x == @x && candidate.y == @y)
    end
  end

  def valid?(origin, bound)
    @x >= origin.x &&
    @x <= bound.x &&
    @y >= origin.y &&
    @y <= bound.y
  end

  def eql?(other)
    @x == other.x && @y == other.y
  end
  alias :== :eql?

  def hash
    -x.hash ^ y.hash
  end
end

class NumberPoints
  attr_reader :number, :points

  def initialize(number, points)
    @number = number
    @points = points
  end
end

class InputReader
  attr_reader :number_points, :symbols, :origin, :bound

  def initialize(file)
    @_file = file
    @number_points = []
    @symbols = Set[]
    @origin = Point.new(0, 0)
    @bound = Point.new(0, 0)
    @_number_reader = ''
    @_number_points = Set[]
  end

  def parse!
    File.readlines(@_file).each_with_index do |row, x|
      self._reset_number_reader
      row.strip.split('').each_with_index do |char, y|
        self._maybe_set_bound(x, y)

        if char.match?(/[[:digit:]]/)
          @_number_reader += char
          @_number_points << Point.new(x, y)
        else
          self._reset_number_reader
          if char != '.'
            @symbols << Point.new(x, y)
          end
        end
      end
    end
  end

  def _reset_number_reader
    if !@_number_reader.empty?
      number = @_number_reader.to_i
      @number_points << NumberPoints.new(number, @_number_points)
    end
    @_number_reader = ''
    @_number_points = Set[]
  end

  def _maybe_set_bound(x, y)
    if @bound.x < x
      @bound = Point.new(x, bound.y)
    end
    if @bound.y < y
      @bound = Point.new(bound.x, y)
    end
  end
end

input_reader = InputReader.new('input')
input_reader.parse!

puts(input_reader.number_points.reduce({}) do |acc, number_points|
  acc[number_points] = number_points.points
    .flat_map(&:candidate_neighbors)
    .filter { |point| point.valid?(input_reader.origin, input_reader.bound) }
  acc
end.filter do |number_points, neighbors|
  neighbors.any? { |neighbor| input_reader.symbols.include?(neighbor) }
end
  .map { |k, _| k.number }
  .sum)

require_relative '1'

class GearInputReader < InputReader
  attr_reader :number_points, :gears, :origin, :bound

  def initialize(file)
    @_file = file
    @number_points = []
    @gears = Set[]
    @origin = Point.new(0, 0)
    @bound = Point.new(0, 0)
    @_number_reader = ''
    @_number_points = []
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
          if char == '*'
            @gears << Point.new(x, y)
          end
        end
      end
    end
  end
end

def gear_ratio(gear_point, input_reader)
  neighbors = gear_point.candidate_neighbors
    .filter { |point| point.valid?(input_reader.origin, input_reader.bound) }
    .reduce(Set[]) { |acc, point| acc << point }
  numbers = input_reader.number_points.filter do |number_point|
    number_point.points.intersect?(neighbors)
  end

  if numbers.size == 2
    numbers.map { |number| number.number }.reduce(:*)
  else
    0
  end
end

input_reader = GearInputReader.new('input')
input_reader.parse!

puts(input_reader.gears.map do |gear_point|
  gear_ratio(gear_point, input_reader)
end.sum)

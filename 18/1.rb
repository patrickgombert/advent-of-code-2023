class Digger
  def initialize
    @exterior = Set[[0, 0]]
    @position = [0, 0]
  end

  def move!(direction, amount)
    case direction
    when 'U'
      (0...amount).each do |x|
        @exterior << [@position[0] - x, @position[1]]
      end
      @position = [@position[0] - amount, @position[1]]
    when 'D'
      (0...amount).each do |x|
        @exterior << [@position[0] + x, @position[1]]
      end
      @position = [@position[0] + amount, @position[1]]
    when 'L'
      (0..amount).each do |y|
        @exterior << [@position[0], @position[1] - y]
      end
      @position = [@position[0], @position[1] - amount]
    when 'R'
      (0..amount).each do |y|
        @exterior << [@position[0], @position[1] + y]
      end
      @position = [@position[0], @position[1] + amount]
    end
  end

  def enclosed_area
    origin = @exterior.sort do |(x1, y1), (x2, y2)|
      if x1 == x2
        y1 <=> y2
      else
        x1 <=> x2
      end
    end.first

    to_process = [[origin[0] + 1, origin[1] + 1]]
    flooded = Set[]
    while !to_process.empty?
      location = to_process.pop
      if @exterior.include?(location)
        flooded << location
      else
        if !flooded.include?(location)
          flooded << location
          neighbors(location).each { |neighbor| to_process << neighbor }
        end
      end
    end

    flooded.size
  end

  private

  def neighbors(point)
    [
      [point[0], point[1] + 1],
      [point[0], point[1] - 1],
      [point[0] + 1, point[1]],
      [point[0] - 1, point[1]],
      [point[0] + 1, point[1] + 1],
      [point[0] + 1, point[1] - 1],
      [point[0] - 1, point[1] + 1],
      [point[0] - 1, point[1] - 1]
    ]
  end
end

digger = Digger.new
File.readlines('input').each do |line|
  direction, amount = line.split(' ')
  digger.move!(direction, amount.to_i)
end
puts digger.enclosed_area

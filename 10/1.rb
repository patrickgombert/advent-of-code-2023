require 'set'

class Position
  attr_reader :x, :y

  def initialize(x, y)
    @x = x
    @y = y
  end

  def transform(delta_x, delta_y)
    Position.new(@x + delta_x, @y + delta_y)
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

class Node
  attr_reader :position, :pipe, :edges

  def initialize(position, pipe)
    @position = position
    @pipe = pipe
    @edges = to_edges(pipe)
  end

  def to_s
    "#{@pipe} - #{@position}"
  end

  def eql?(other)
    @position == other.position
  end
  alias :== :eql?

  def hash
    @position.hash
  end

  private

  def to_edges(pipe)
    case pipe
    when '|'
      [@position.transform(-1, 0), @position.transform(1, 0)]
    when '-'
      [@position.transform(0, 1), @position.transform(0, -1)]
    when 'L'
      [@position.transform(-1, 0), @position.transform(0, 1)]
    when 'J'
      [@position.transform(-1, 0), @position.transform(0, -1)]
    when '7'
      [@position.transform(1, 0), @position.transform(0, -1)]
    when 'F'
      [@position.transform(1, 0), @position.transform(0, 1)]
    when '.'
      []
    when 'S'
      [@position.transform(-1, 0),
       @position.transform(1, 0),
       @position.transform(0, -1),
       @position.transform(0, 1)]
    end
  end
end

def find_loop(node, previous_node, nodes, path)
  loop do
    if node.pipe == '.'
      return nil
    end

    next_positions = node.edges - [previous_node.position]
    next_node = nodes[next_positions[0]]
    if next_node.pipe == 'S'
      if path.empty?
        next_node = nodes[next_positions[1]]
      else
        path << next_node
        return path
      end
    end
    if path.include?(next_node)
      return nil
    else
      path << node
      previous_node = node
      node = next_node
    end
  end
end

def longest_loop_from_file(file_path)
  starting_node = nil
  nodes = {}
  File.readlines('input').each_with_index do |row, x|
    row.strip.split('').each_with_index do |char, y|
      position = Position.new(x, y)
      node = Node.new(position, char)
      nodes[position] = node
      if char == 'S'
        starting_node = node
      end
    end
  end

  paths = starting_node.edges.map do |edge|
    node = nodes[edge]
    if !node.nil?
      find_loop(nodes[edge], starting_node, nodes, [])
    end
  end.filter { |n| !n.nil? }
  paths.sort_by { |path| path.size }.reverse[0]
end

puts (longest_loop_from_file('input') / 2.0).ceil

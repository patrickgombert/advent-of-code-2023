require_relative '1'

class Enclosure
  def initialize(nodes, loop_nodes)
    @nodes = nodes
    @loop_nodes = replace_s!(loop_nodes)
    @x_y_lookup = loop_nodes.reduce({}) do |acc, node|
      ys = acc.fetch(node.position.x, {})
      ys[node.position.y] = node
      acc[node.position.x] = ys
      acc
    end
    @rows = {}
    @cols = {}
  end

  def enclosed_count
    x = @nodes.keys.map(&:x).max
    y = @nodes.keys.map(&:y).max

    (0..x).reduce(0) do |acc, row|
      (0..y).reduce([acc, false]) do |(row_acc, inside), col|
        node = @nodes[Position.new(row, col)]
        in_loop = !@x_y_lookup.fetch(row, {})[col].nil?

        if in_loop && flip?(node.pipe)
          inside = !inside
        end

        if !in_loop && inside
          row_acc = row_acc + 1
        end

        [row_acc, inside]
      end[0]
    end
  end

  private

  def flip?(pipe)
    pipe == "|" || pipe == "7" || pipe == "F"
  end

  def replace_s!(loop_nodes)
    i = loop_nodes.index { |n| n.pipe == 'S' }
    s = loop_nodes[i]
    nodes = loop_nodes - [s]
    s.resolve_s!(nodes)
    loop_nodes
  end
end

nodes, loop_nodes = longest_loop_from_file('test')
enclosure = Enclosure.new(nodes, loop_nodes)
puts enclosure.enclosed_count

require_relative '1'

class Lens
  attr_reader :label, :focal_length

  def initialize(label, focal_length)
    @label = label
    @focal_length = focal_length
  end
end

def process(boxes, instruction)
  if instruction.include?('-')
    label = instruction.split('-')[0]
    box = boxes[h(label)] ||= []
    box.delete_if { |lense| lense.label == label }
  else
    label, focal_length = instruction.split('=')
    box = boxes[h(label)] ||= []
    lens = Lens.new(label, focal_length.to_i)
    existing_index = box.index { |lens| lens.label == label }
    if existing_index
      box[existing_index] = lens
    else
      box << lens
    end
  end
end

boxes = {}
read('input')
  .each { |instruction| process(boxes, instruction) }

puts(boxes.sum do |box, lenses|
  lenses.each_with_index.sum do |lense, i|
    (box + 1) * (i + 1) * lense.focal_length
  end
end)

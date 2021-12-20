import _ from 'lodash'

const input = `
9C0141080250320F1802104A08
`

interface Instruction {
  version: number
  typeId: number
  value: number
  subInstructions?: Instruction[]
}

const binary = input
  .trim()
  .split('')
  .map(digit => parseInt(digit, 16).toString(2).padStart(4, '0'))
  .join('')

const transformations: ((instructions: Instruction[]) => number)[] = [
  instructions => _.sum(instructions.map(instruction => instruction.value)),
  instructions => instructions.reduce((product, instruction) => product * instruction.value, 1),
  instructions => _.min(instructions.map(instruction => instruction.value))!,
  instructions => _.max(instructions.map(instruction => instruction.value))!,
  _instructions => -1,
  instructions => (instructions[0].value > instructions[1].value ? 1 : 0),
  instructions => (instructions[0].value < instructions[1].value ? 1 : 0),
  instructions => (instructions[0].value === instructions[1].value ? 1 : 0)
]

function parseNumber(binary: string): [number, string] {
  let remainder = binary
  let number = ''

  while (true) {
    const [notLastDigit, remainderPostLastDigit] = read(remainder, 1)

    const [digit, remainderPostDigit] = readRaw(remainderPostLastDigit, 4)
    number += digit
    remainder = remainderPostDigit

    if (!notLastDigit) break
  }

  return [parseInt(number, 2), remainder]
}

function parseOperation(binary: string): [Instruction[], string] {
  const [lengthTypeId, remainderPostType] = read(binary, 1)

  if (lengthTypeId) {
    const [subInstructionsLength, remainderPostLength] = read(remainderPostType, 11)

    return parseOperations(remainderPostLength, subInstructionsLength)
  } else {
    const [subInstructionsLength, remainderPostLength] = read(remainderPostType, 15)
    const [subInstructions, _blank] = parseOperations(
      remainderPostLength.slice(0, subInstructionsLength)
    )

    return [subInstructions, remainderPostLength.slice(subInstructionsLength)]
  }
}

function parseOperations(binary: string, count = Infinity): [Instruction[], string] {
  let remainder = binary
  const instructions: Instruction[] = []

  while (remainder.length && instructions.length < count) {
    const [instruction, remainderPostPacket] = parsePacket(remainder)
    instructions.push(instruction)
    remainder = remainderPostPacket
  }

  return [instructions, remainder]
}

function parsePacket(binary: string): [Instruction, string] {
  const [version, remainderPostVersion] = read(binary, 3)
  const [typeId, remainderPostType] = read(remainderPostVersion, 3)

  if (typeId === 4) {
    const [number, remainderPostNumber] = parseNumber(remainderPostType)
    return [{version: version, typeId: typeId, value: number}, remainderPostNumber]
  } else {
    const [subInstructions, remainderPostSubInstructions] = parseOperation(remainderPostType)
    return [
      {
        version: version,
        typeId: typeId,
        subInstructions: subInstructions,
        value: transformations[typeId](subInstructions)
      },
      remainderPostSubInstructions
    ]
  }
}

function readRaw(binary: string, length: number): [string, string] {
  return [binary.slice(0, length), binary.slice(length)]
}

function read(binary: string, length: number): [number, string] {
  const [raw, remainder] = readRaw(binary, length)
  return [parseInt(raw, 2), remainder]
}

console.log(parsePacket(binary)[0].value)

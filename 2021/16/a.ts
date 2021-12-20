import _ from 'lodash'

const input = `
A0016C880162017C3686B18A3D4780
`

interface Instruction {
  version: number
  typeId: number
  value?: number
  subInstructions?: Instruction[]
}

const binary = input
  .trim()
  .split('')
  .map(digit => parseInt(digit, 16).toString(2).padStart(4, '0'))
  .join('')

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
  const [typeId, remainder] = read(remainderPostVersion, 3)

  switch (typeId) {
    case 4:
      const [number, remainderPostNumber] = parseNumber(remainder)
      return [{version: version, typeId: typeId, value: number}, remainderPostNumber]
    default:
      const [subInstructions, remainderPostOperation] = parseOperation(remainder)
      return [
        {version: version, typeId: typeId, subInstructions: subInstructions},
        remainderPostOperation
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

function sumTree(tree: Instruction): number {
  if (!tree.subInstructions) return tree.version

  return tree.version + _.sum(tree.subInstructions.map(subTree => sumTree(subTree)))
}

console.log(sumTree(parsePacket(binary)[0]))

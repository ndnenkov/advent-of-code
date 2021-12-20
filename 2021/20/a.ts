import _ from 'lodash'

const input = `
..#.#..#####.#.#.#.###.##.....###.##.#..###.####..#####..#....#..#..##..###..######.###...####..#..#####..##..#.#####...##.#.#..#.##..#.#......#.###.######.###.####...#.##.##..#..#..#####.....#.#....###..#.##......#.....#..#..#..##..#...##.######.####.####.#.#...#.......#..#.#.#...####.##.#......#..#...##.#.##..#...##.#.##..###.#......#.#.......#.#.#.####.###.##...#.....####.#..#..#.##.#....##..#.####....##...##..#...#......#.#.......#.......##..####..#...#.#.#...##..#.#..###..#####........#..####......#..#

#..#.
#....
##..#
..#..
..###
`

const algorithm = input.trim().split('\n\n')[0].split('') as ('.' | '#')[]
const image = input.trim().split('\n\n')[1]

function padImage(image: string) {
  const lineLength = image.split('\n')[0].length
  const oneLinePadding = '.'.repeat(lineLength + 4)
  const twoLinesPadding = `${oneLinePadding}\n${oneLinePadding}`

  const paddedLines = image
    .split('\n')
    .map(line => `..${line}..`)
    .join('\n')

  return `${twoLinesPadding}\n${paddedLines}\n${twoLinesPadding}`
}

function enhanced(
  pixelatedImage: ('.' | '#')[][],
  x: number,
  y: number,
  algorithm: ('.' | '#')[],
  blank: '.' | '#'
): '.' | '#' {
  const section = [
    pixelatedImage[y - 1]?.[x - 1],
    pixelatedImage[y - 1]?.[x],
    pixelatedImage[y - 1]?.[x + 1],
    pixelatedImage[y][x - 1],
    pixelatedImage[y][x],
    pixelatedImage[y][x + 1],
    pixelatedImage[y + 1]?.[x - 1],
    pixelatedImage[y + 1]?.[x],
    pixelatedImage[y + 1]?.[x + 1]
  ].map(pixel => pixel ?? blank)

  return algorithm[parseInt(section.join('').replace(/\./g, '0').replace(/#/g, '1'), 2)]
}

function enhance(pixelatedImage: ('.' | '#')[][], algorithm: ('.' | '#')[], blank: '.' | '#') {
  return pixelatedImage.map((line, y) =>
    line.map((_pixel, x) => enhanced(pixelatedImage, x, y, algorithm, blank))
  )
}

const pixelatedImage = padImage(image)
  .split('\n')
  .map(line => line.split('') as ('.' | '#')[])

const swap = _.first(algorithm) === '#' && _.last(algorithm) === '.'
const once = enhance(pixelatedImage, algorithm, '.')
const twice = enhance(once, algorithm, swap ? '#' : '.')
const count = _.flatten(twice).filter(pixel => pixel === '#').length
console.log(count)

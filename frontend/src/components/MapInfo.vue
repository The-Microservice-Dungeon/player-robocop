<template>
  <div class="infoCard">
    <h3>Map</h3>
    <input
      v-model="camera.x"
      step="32"
      type="number"
      @change="drawMapWithCamera"
    >
    <canvas
      ref="mapCanvas"
      class="mapCanvas"
    />
  </div>
</template>

<script>
import mapTiles from '@/assets/mapTiles.png'

const mapSize = 35

export default {
  name: 'MapInfo',
  data () {
    return {
      tileResolution: 64,
      cols: mapSize * 2,
      rows: mapSize * 2,
      mapWidth: undefined,
      mapHeight: undefined,
      ctx: undefined,
      tileAtlas: undefined,
      map: [],
      camera: {},
    }
  },
  mounted () {
    this.loadTiles()
    .then(() => {
      this.initMap()
      this.initCanvas()
      this.drawMapWithCamera()
    })
  },
  methods: {
    loadTiles () {
      let img = new Image()

      let loadingPromise = new Promise((resolve, reject) => {
        img.onload = function () {
          this.tileAtlas = img
          resolve(img)
        }.bind(this)

        img.onerror = function () {
          reject(new Error('Could not load image: ' + mapTiles))
        }
      })

      img.src = mapTiles

      return loadingPromise
    },
    initMap () {
      this.map = Array.from({ length: this.cols * this.rows }, (x, i) => i % 3 === 0 ? 1 : 2)
      this.mapWidth = this.cols * this.tileResolution / 2
      this.mapHeight = this.rows * this.tileResolution / 2
      console.log(this.map)
    },
    getTile (col, row) {
      return this.map[row * this.cols + col]
    },
    setTile (col, row, value) {
      this.map[row * this.cols + col] = value
    },
    initCanvas () {
      const canvas = this.$refs['mapCanvas']
      this.ctx = canvas.getContext('2d')
      this.ctx.canvas.width = this.mapWidth
      this.ctx.canvas.height = this.mapHeight

      this.camera = {
        x: this.mapWidth / 2,
        y: this.mapHeight / 2,
        width: this.mapWidth,
        height: this.mapHeight,
      }
    },
    drawMap () {
      for (let col = 0; col < this.cols; col++) {
        for (let row = 0; row < this.rows; row++) {
          let tile = this.getTile(col, row)
          this.drawTile(tile, col * this.tileResolution, row * this.tileResolution)
        }
      }
    },
    drawMapWithCamera () {
      let startCol = Math.floor(this.camera.x / this.tileResolution)
      let endCol = startCol + (this.camera.width / this.tileResolution)
      let startRow = Math.floor(this.camera.y / this.tileResolution)
      let endRow = startRow + (this.camera.height / this.tileResolution)

      let offsetX = -this.camera.x + startCol * this.tileResolution
      let offsetY = -this.camera.y + startRow * this.tileResolution

      for (let col = startCol; col <= endCol; col++) {
        for (let row = startRow; row <= endRow; row++) {
          let tile = this.getTile(col, row)
          let x = (col - startCol) * this.tileResolution + offsetX
          let y = (row - startRow) * this.tileResolution + offsetY
          this.drawTile(tile, x, y)
        }
      }
    },
    drawTile (tile, x, y) {
      if (tile !== 0) { // 0 => empty tile
        this.ctx.drawImage(
          this.tileAtlas, // image
          (tile - 1) * this.tileResolution, // source x
          0, // source y
          this.tileResolution, // source width
          this.tileResolution, // source height
          Math.round(x), // target x
          Math.round(y), // target y
          this.tileResolution, // target width
          this.tileResolution // target height
        )
      }
    },
  },
}
</script>

<style scoped>
.mapCanvas {
  width: 50%;
}
</style>

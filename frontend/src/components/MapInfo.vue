<template>
  <div class="infoCard">
    <h3>Map</h3>
    <label for="xOffset">
      X Offset
      <input
        id="xOffset"
        v-model="camera.x"
        step="32"
        :min="0"
        :max="calculateMaxHorizontalScroll()"
        type="number"
        @change="render"
      >
    </label>
    <label for="yOffset">
      Y Offset
      <input
        id="yOffset"
        v-model="camera.y"
        step="32"
        :min="-mapHeight / 2"
        :max="mapHeight / 2"
        type="number"
        @change="render"
      >
    </label>
    <label for="zoom">
      Zoom
      <input
        id="zoom"
        v-model="zoomLevel"
        step="0.1"
        type="range"
        min="1"
        max="4"
        @change="render"
      >
    </label>
    <canvas
      ref="mapCanvas"
      class="mapCanvas"
    />
  </div>
</template>

<script>
import mapTiles from '@/assets/mapTiles.png'

export default {
  name: 'MapInfo',
  props: {
    playerCount: {
      type: Number,
      default: 20,
    },
  },
  data () {
    return {
      tileResolution: 32,
      cols: undefined,
      rows: undefined,
      mapWidth: undefined,
      mapHeight: undefined,
      ctx: undefined,
      tileAtlas: undefined,
      layers: [
        [], // gravity
        [], // stations / resources
        [], // robots
      ],
      camera: {},
      zoomLevel: '1',
    }
  },
  computed: {
    mapSize: function () {
      if (this.playerCount < 10) return 15
      if (this.playerCount < 20) return 20
      return 35
    },
  },
  mounted () {
    this.loadTiles()
      .then(() => {
        this.setMapDimensions()
        this.buildMap()
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
    render () {
      this.setMapDimensions()
      this.updateCanvas()
      this.drawMapWithCamera()
    },
    setMapDimensions () {
      this.cols = this.mapSize * 2
      this.rows = this.mapSize * 2
      this.mapWidth = this.cols * this.tileResolution / this.zoomLevel
      this.mapHeight = this.rows * this.tileResolution / this.zoomLevel
      this.calculateMaxHorizontalScroll()
      this.clampMaxHorizontalScroll()
    },
    buildMap () {
      const totalLength = this.cols * this.rows
      // random Gravity Map
      this.layers[0] = Array.from({ length: totalLength }, (x, i) => {
        // borders
        if (this.tileIsBorder(totalLength, i)) return 1
        // center of map
        return i % 3 === 0 ? 1 : 0
      })
      // random Station/Resources Map
      this.layers[1] = Array.from({ length: totalLength }, (x, i) => {
        if (!this.tileIsBorder(totalLength, i)) {
          let shouldDraw = this.getRandomInt(1, 4)
          if (shouldDraw === 1) return this.getRandomInt(3, 8)
        }
        return -1
      })
      // random robots
      this.layers[2] = Array.from({ length: totalLength }, (x, i) => {
        if (!this.tileIsBorder(totalLength, i)) {
          let shouldDraw = this.getRandomInt(1, 25)
          if (shouldDraw === 1) return 10
        }
        return -1
      })
      console.log(this.layers)
    },
    getTile (layer, col, row) {
      return this.layers[layer][row * this.cols + col]
    },
    setTile (layer, col, row, value) {
      this.layers[layer][row * this.cols + col] = value
    },
    tileIsBorder (totalLength, i) {
      if (i >= 0 && i < this.cols) return true
      if (i % this.mapSize === 0 && i / 2 % this.mapSize === 0) return true
      if ((i + 1) % this.mapSize === 0 && (i + 1) / 2 % this.mapSize === 0) return true
      return totalLength - i < this.mapSize * 2
    },
    initCanvas () {
      const canvas = this.$refs['mapCanvas']
      this.ctx = canvas.getContext('2d')
      this.ctx.canvas.width = this.mapWidth
      this.ctx.canvas.height = this.mapHeight

      this.camera = {
        x: 0,
        y: 0,
        width: this.mapWidth,
        height: this.mapHeight,
      }
    },
    updateCanvas () {
      this.ctx.canvas.width = this.mapWidth
      this.ctx.canvas.height = this.mapHeight

      this.camera.width = this.mapWidth
      this.camera.height = this.mapHeight
    },
    drawMap () {
      for (let layer in this.layers) {
        for (let col = 0; col < this.cols; col++) {
          for (let row = 0; row < this.rows; row++) {
            let tile = this.getTile(layer, col, row)
            this.drawTile(tile, col * this.tileResolution, row * this.tileResolution)
          }
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

      for (let layer in this.layers) {
        for (let col = startCol; col <= endCol; col++) {
          for (let row = startRow; row <= endRow; row++) {
            let tile = this.getTile(layer, col, row)
            let x = (col - startCol) * this.tileResolution + offsetX
            let y = (row - startRow) * this.tileResolution + offsetY
            this.drawTile(tile, x, y)
          }
        }
      }
    },
    drawTile (tile, x, y) {
      if (tile !== -1) { // -1 => empty tile
        this.ctx.drawImage(
          this.tileAtlas, // image
          tile * this.tileResolution, // source x
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
    calculateMaxHorizontalScroll () {
      let maxScroll = 0
      // This is f* ugly. TODO: Figure out a correlation between zoom level and allowed scroll
      if (this.zoomLevel === '1.1') maxScroll = 192
      if (this.zoomLevel === '1.2') maxScroll = 384
      if (this.zoomLevel === '1.3') maxScroll = 512
      if (this.zoomLevel === '1.4') maxScroll = 640
      if (this.zoomLevel === '1.5') maxScroll = 736
      if (this.zoomLevel === '1.6') maxScroll = 832
      if (this.zoomLevel === '1.7') maxScroll = 928
      if (this.zoomLevel === '1.8') maxScroll = 992
      if (this.zoomLevel === '1.9') maxScroll = 1056
      if (this.zoomLevel === '2') maxScroll = 1120
      if (this.zoomLevel === '2.1') maxScroll = 1184
      if (this.zoomLevel === '2.2') maxScroll = 1216
      if (this.zoomLevel === '2.3') maxScroll = 1280
      if (this.zoomLevel === '2.4') maxScroll = 1312
      if (this.zoomLevel === '2.5') maxScroll = 1344
      if (this.zoomLevel === '2.6') maxScroll = 1376
      if (this.zoomLevel === '2.7') maxScroll = 1408
      if (this.zoomLevel === '2.8') maxScroll = 1440
      if (this.zoomLevel === '2.9') maxScroll = 1472
      if (this.zoomLevel === '3') maxScroll = 1504
      if (this.zoomLevel === '3.1') maxScroll = 1520
      if (this.zoomLevel === '3.2') maxScroll = 1536
      if (this.zoomLevel === '3.3') maxScroll = 1560
      if (this.zoomLevel === '3.4') maxScroll = 1596
      if (this.zoomLevel === '3.5') maxScroll = 1600
      if (this.zoomLevel === '3.6') maxScroll = 1616
      if (this.zoomLevel === '3.7') maxScroll = 1632
      if (this.zoomLevel === '3.8') maxScroll = 1660
      if (this.zoomLevel === '3.9') maxScroll = 1664
      if (this.zoomLevel === '4') maxScroll = 1678
      return maxScroll
    },
    clampMaxHorizontalScroll () {
      const maxScroll = this.calculateMaxHorizontalScroll()
      if (this.camera.x > maxScroll) this.camera.x = maxScroll
    },
    getRandomInt (min, max) {
      min = Math.ceil(min)
      max = Math.floor(max)
      return Math.floor(Math.random() * (max - min)) + min
    },
  },
}
</script>

<style scoped>
.mapCanvas {
  width: 50%;
}
</style>

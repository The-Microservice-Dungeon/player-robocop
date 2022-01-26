<template>
  <div :class="maxWidth ? 'maxWidth infoCard' : 'infoCard'">
    <h3>Map</h3>
    <div class="controls">
      <div class="sliders">
        <label for="xOffset">
          X Offset: {{ camera.x }}
          <input
            id="xOffset"
            v-model="camera.x"
            class="slider"
            step="8"
            :min="0"
            :max="calculateMaxScroll()"
            type="range"
            @input="render"
          >
        </label>
        <label for="yOffset">
          Y Offset: {{ camera.y }}
          <input
            id="yOffset"
            v-model="camera.y"
            class="slider"
            step="8"
            :min="0"
            :max="calculateMaxScroll()"
            type="range"
            @input="render"
          >
        </label>
        <label for="zoom">
          Zoom: {{ zoomLevel }}
          <input
            id="zoom"
            v-model="zoomLevel"
            class="slider"
            step="0.1"
            type="range"
            min="1"
            max="4"
            @input="render"
          >
        </label>
      </div>
      <div>
        <button
          class="resetButton"
          @click="mapOverview"
        >
          View all
        </button>
        <button
          class="resetButton"
          @click="resetCamera"
        >
          Reset View
        </button>
      </div>
    </div>
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
    isMobile: {
      type: Boolean,
      default: false,
    },
  },
  data () {
    return {
      maxWidth: !this.isMobile,
      playerCount: 20,
      tileResolution: 64,
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
      zoomLevel: 1.5,
    }
  },
  computed: {
    mapSize: function () {
      if (this.playerCount < 10) return 15
      if (this.playerCount < 20) return 20
      return 35
    },
  },
  watch: {
    isMobile () {
      this.maxWidth = !this.isMobile
    },
  },
  mounted () {
    this.loadTiles()
      .then(() => {
        this.setMapDimensions()
        this.buildMap()
        this.initCanvas()
        this.initCamera()
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
      this.calculateMaxScroll()
      this.clampScroll()
    },
    buildMap () {
      const totalLength = this.cols * this.rows
      // random Gravity Map
      this.layers[0] = Array.from({ length: totalLength }, (x, i) => {
        // borders
        if (this.tileIsBorder(totalLength, i)) return 1
        if (this.getCenter() === i) return 2
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
    },
    initCamera () {
      this.camera = {
        x: 720,
        y: 720,
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
    calculateMaxScroll () {
      return this.cols * this.tileResolution - this.mapWidth
    },
    clampScroll () {
      const maxScroll = this.calculateMaxScroll()
      if (this.camera.x > maxScroll) this.camera.x = maxScroll
      if (this.camera.y > maxScroll) this.camera.y = maxScroll
    },
    mapOverview () {
      this.camera.y = 0
      this.camera.x = 0
      this.zoomLevel = 1
      this.render()
    },
    resetCamera () {
      this.zoomLevel = 1.5
      this.initCamera()
      this.render()
    },
    getRandomInt (min, max) {
      min = Math.ceil(min)
      max = Math.floor(max)
      return Math.floor(Math.random() * (max - min)) + min
    },
    getCenter () {
      return this.mapSize * this.cols + this.mapSize
    },
  },
}
</script>

<style scoped>
.maxWidth {
  max-height: 100vh;
  width: 55vw;
}

.controls {
  display: flex;
  justify-content: space-evenly;
}

.sliders {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.slider {
  width: 50%;
}

.resetButton {
  height: 2rem;
  margin-top: 1rem;
}

.mapCanvas {
  max-height: 100vh;
  width: 95%;
}
</style>

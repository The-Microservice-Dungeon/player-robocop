<template>
  <div :class="maxWidth ? 'maxWidth infoCard' : 'infoCard'">
    <h3>Map</h3>
    <div class="controls">
      <div class="planar">
        <planar-range
          v-if="renderPlanarPosition"
          class="positionPicker"
        >
          <planar-range-thumb
            x="0.5"
            y="0.5"
            @change="updateCameraPosition"
          />
        </planar-range>
      </div>
      <div class="controlInfos">
        <div class="sliders">
          <label for="xOffset">
            X: {{ camera.x }}
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
            Y: {{ camera.y }}
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
              max="10"
              @input="render"
            >
          </label>
        </div>
        <div class="buttons">
          <button
            class="button"
            @click="mapOverview"
          >
            View all
          </button>
          <button
            class="button"
            @click="centerCamera"
          >
            Center View
          </button>
          <button
            class="button"
            @click="resetCamera"
          >
            Reset View
          </button>
        </div>
      </div>
    </div>
    <bounce-loader
      v-if="firstLoad"
      :color="'red'"
    />
    <canvas
      v-else
      ref="mapCanvas"
      class="mapCanvas"
    />
  </div>
</template>

<script>
import mapTiles from '@/assets/mapTiles.png'
import { apiGet } from '@/utils'
import BounceLoader from 'vue-spinner/src/BounceLoader.vue'

// eslint-disable-next-line no-unused-vars
const planarSlider = require('planar-range')

export default {
  name: 'MapInfo',
  components: {
    BounceLoader,
  },
  props: {
    isMobile: {
      type: Boolean,
      default: false,
    },
  },
  data () {
    return {
      firstLoad: true,
      maxWidth: !this.isMobile,
      mapSize: 15,
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
      zoomLevel: 4,
      renderPlanarPosition: true,
    }
  },
  watch: {
    isMobile () {
      this.maxWidth = !this.isMobile
    },
  },
  mounted () {
    this.fetchMapData()
  },
  methods: {
    initializeMapOnLoad () {
      this.loadTiles()
        .then(() => {
          this.setMapDimensions()
          // this.buildMap()
          this.initCanvas()
          this.initCamera()
          this.drawMapWithCamera()
        })
    },
    fetchMapData () {
      apiGet('/map')
        .then((response) => {
          if (response.status !== 200) throw new Error('Unexpected Response ' + response.status)
          return response
        })
        .then(response => response.json())
        .then(response => {
          if (response) {
            console.log(response)
            this.mapSize = response.mapSize
            this.layers[0] = response.gravity
            this.layers[1] = response.types
            this.layers[2] = response.robots

            if (this.firstLoad) {
              this.firstLoad = false
              this.initializeMapOnLoad()
            } else {
              this.drawMapWithCamera()
            }
          }
        })
        .catch(e => {
          this.firstLoad = false
          console.warn(e)
        })
    },
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
      const xyOffset = Math.floor(this.calculateMaxScroll() / 2 + this.tileResolution / 2)
      this.camera = {
        x: xyOffset,
        y: xyOffset,
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
      return Math.floor(this.cols * this.tileResolution - this.mapWidth)
    },
    clampScroll () {
      const maxScroll = this.calculateMaxScroll()
      if (this.camera.x > maxScroll) this.camera.x = maxScroll
      if (this.camera.y > maxScroll) this.camera.y = maxScroll
      if (this.camera.x < 0) this.camera.x = 0
      if (this.camera.y < 0) this.camera.y = 0
    },
    mapOverview () {
      this.camera.y = 0
      this.camera.x = 0
      this.zoomLevel = 1
      this.resetPositionPicker()
      this.render()
    },
    centerCamera () {
      this.resetPositionPicker()
      this.initCamera()
      this.render()
    },
    resetCamera () {
      this.zoomLevel = 4
      this.resetPositionPicker()
      this.setMapDimensions()
      this.initCamera()
      this.render()
    },
    resetPositionPicker () {
      this.renderPlanarPosition = false
      this.$nextTick(() => {
        this.renderPlanarPosition = true
      })
    },
    getRandomInt (min, max) {
      min = Math.ceil(min)
      max = Math.floor(max)
      return Math.floor(Math.random() * (max - min)) + min
    },
    getCenter () {
      return this.mapSize * this.cols + this.mapSize
    },
    updateCameraPosition (e) {
      this.camera.x = Math.floor((this.cols * this.tileResolution * e.detail.x) - this.mapWidth / 2)
      this.camera.y = Math.floor((this.cols * this.tileResolution * e.detail.y) - this.mapWidth / 2)
      this.render()
    },
  },
}
</script>

<style scoped>
.maxWidth {
  width: 55vw;
  max-width: 65vh;
}

.controls {
  display: flex;
  justify-content: space-evenly;
}

.controlInfos {
  display: flex;
  flex-direction: column;
  justify-content: space-around;
}

.sliders {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.slider {
  width: 45%;
  height: 12px;
}

.positionPicker {
  background-size: 10px 10px;
  background-image:
    linear-gradient(to right, white 1px, transparent 1px),
    linear-gradient(to bottom, white 1px, transparent 1px);
}

.buttons {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: space-between;
  height: 8rem;
}

.button {
  height: 2rem;
}

.mapCanvas {
  max-height: 100vh;
  width: 95%;
}

planar-range-thumb {
  background: url('~@/assets/logo.webp');
  background-size: contain;
  border: 1px solid red;
  background-color: red;
}
</style>

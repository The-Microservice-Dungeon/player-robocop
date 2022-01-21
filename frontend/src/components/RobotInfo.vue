<template>
  <div class="infoCard">
    <h3>Robot Info</h3>
    <div
      v-if="robots.length > 0"
      class="robots"
    >
      <div
        v-for="(robot, index) in robots"
        :key="index"
        class="robot"
      >
        <div>
          <h4>General Info Robot {{ index + 1 }}</h4>
          <span>
            Health: {{ robot.health }}
          </span>
          <span>
            Energy: {{ robot.energy }}
          </span>
        </div>
        <div>
          <h4>Inventory</h4>
          <span>
            Coal: {{ robot.inventory.coal }} Iron: {{ robot.inventory.iron }}
          </span>
          <br>
          <span>
            Ruby: {{ robot.inventory.ruby }} Platin: {{ robot.inventory.platin }}
          </span>
        </div>
        <div>
          <h4>Stats</h4>
          <table>
            <thead>
              <tr>
                <th>Name</th>
                <th>Level</th>
                <th>Value</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="(stat, key) in robot.stats"
                :key="key"
              >
                <td>
                  {{ key }}
                </td>
                <td>
                  {{ stat.level }}
                </td>
                <td>
                  {{ stat.value }}
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
    <h3 v-else>
      No Robots
    </h3>
  </div>
</template>

<script>
export default {
  name: 'RobotInfo',
  data () {
    return {
      robots: [
          {
            health: 100,
            energy: 95,
            inventory: {
              coal: 0,
              iron: 1,
              ruby: 2,
              platin: 3,
            },
            stats: {
              STORAGE: {
                level: 1,
                value: 20,
              },
              HEALTH: {
                level: 1,
                value: 10,
              },
              DAMAGE: {
                level: 1,
                value: 1,
              },
              MINING_SPEED: {
                level: 1,
                value: 2,
              },
              MINING: {
                level: 1,
                value: 'COAL',
              },
              MAX_ENERGY: {
                level: 1,
                value: 20,
              },
              ENERGY_REGEN: {
                level: 1,
                value: 4,
              },
            },
        },
          {
            health: 100,
            energy: 95,
            inventory: {
              coal: 0,
              iron: 1,
              ruby: 2,
              platin: 3,
            },
            stats: {
              STORAGE: {
                level: 1,
                value: 20,
              },
              HEALTH: {
                level: 1,
                value: 10,
              },
              DAMAGE: {
                level: 1,
                value: 1,
              },
              MINING_SPEED: {
                level: 1,
                value: 2,
              },
              MINING: {
                level: 1,
                value: 'COAL',
              },
              MAX_ENERGY: {
                level: 1,
                value: 20,
              },
              ENERGY_REGEN: {
                level: 1,
                value: 4,
              },
            },
        },
      ],
    }
  },
}
</script>

<style scoped>
.robots {
  overflow-y: auto;
  max-height: 100%;
}

.robot {
  margin-top: 0.5rem;
  border: 1px solid white;
  border-radius: 32px;
  padding: 8px;
}
</style>

const replicaSetConfig = {
  _id: "rs0",
  members: [
    { _id: 0, host: "mongo-rs.local:27017", priority: 2 },
    { _id: 1, host: "mongo-rs.local:27018", priority: 1 },
    { _id: 2, host: "mongo-rs.local:27019", priority: 1 }
  ]
};

let currentConfig = null;
try {
  currentConfig = rs.conf();
} catch (error) {
  rs.initiate(replicaSetConfig);
}

if (currentConfig) {
  const currentHosts = currentConfig.members.map(member => member.host).join(",");
  const desiredHosts = replicaSetConfig.members.map(member => member.host).join(",");
  if (currentHosts !== desiredHosts) {
    let writablePrimary = false;
    for (let attempt = 0; attempt < 90; attempt++) {
      try {
        if (db.hello().isWritablePrimary) {
          writablePrimary = true;
          break;
        }
      } catch (error) {
        // Wait until a primary is available to accept the reconfiguration.
      }
      sleep(1000);
    }
    if (!writablePrimary) {
      throw new Error("No primary available to update replica-set member hostnames");
    }
    rs.reconfig({ ...replicaSetConfig, version: currentConfig.version + 1 });
  }
}

let primaryReady = false;
for (let attempt = 0; attempt < 90; attempt++) {
  try {
    if (db.hello().isWritablePrimary) {
      primaryReady = true;
      break;
    }
  } catch (error) {
    // Election may not have completed yet.
  }
  sleep(1000);
}

if (!primaryReady) {
  throw new Error("Replica set did not elect a primary within 90 seconds");
}

const appdb = db.getSiblingDB("esportsdb");

const validators = {
  players: {
    $jsonSchema: {
      bsonType: "object",
      required: ["_id", "type", "nickname", "role"],
      properties: {
        _id: { bsonType: "long" },
        type: { bsonType: "string", enum: ["PRO", "AMATEUR"] },
        nickname: { bsonType: "string", minLength: 1 },
        role: { bsonType: "string", minLength: 1 },
        salary: { bsonType: ["decimal", "null"] },
        sponsor: { bsonType: ["string", "null"] },
        rank: { bsonType: ["string", "null"] },
        hoursPlayed: { bsonType: ["int", "long", "null"], minimum: 0 }
      }
    }
  },
  teams: {
    $jsonSchema: {
      bsonType: "object",
      required: ["_id", "name", "region", "playerIds"],
      properties: {
        _id: { bsonType: "long" },
        name: { bsonType: "string", minLength: 1 },
        region: { bsonType: "string", minLength: 1 },
        playerIds: { bsonType: "array", maxItems: 7, uniqueItems: true, items: { bsonType: "long" } }
      }
    }
  },
  tournaments: {
    $and: [
      {
        $jsonSchema: {
          bsonType: "object",
          required: ["_id", "type", "name", "status", "maxTeams", "registeredTeamIds"],
          properties: {
            _id: { bsonType: "long" },
            type: { bsonType: "string", enum: ["LAN", "ONLINE"] },
            name: { bsonType: "string", minLength: 1 },
            status: { bsonType: "string", enum: ["UPCOMING", "REGISTRATION_OPEN", "REGISTRATION_CLOSED", "IN_PROGRESS", "COMPLETED", "CANCELLED"] },
            maxTeams: { bsonType: ["int", "long"], minimum: 2 },
            registeredTeamIds: { bsonType: "array", uniqueItems: true, items: { bsonType: "long" } }
          }
        }
      },
      { $expr: { $lte: [{ $size: { $ifNull: ["$registeredTeamIds", []] } }, "$maxTeams"] } }
    ]
  },
  groups: {
    $jsonSchema: {
      bsonType: "object",
      required: ["_id", "name", "teamIds"],
      properties: {
        _id: { bsonType: "long" },
        name: { bsonType: "string", minLength: 1 },
        tournamentId: { bsonType: ["long", "null"] },
        teamIds: { bsonType: "array", uniqueItems: true, items: { bsonType: "long" } }
      }
    }
  },
  matches: {
    $jsonSchema: {
      bsonType: "object",
      required: ["_id", "team1Score", "team2Score", "status"],
      properties: {
        _id: { bsonType: "long" },
        team1Id: { bsonType: ["long", "null"] },
        team2Id: { bsonType: ["long", "null"] },
        team1Score: { bsonType: "int", minimum: 0 },
        team2Score: { bsonType: "int", minimum: 0 },
        matchDate: { bsonType: ["date", "null"] },
        status: { bsonType: "string", enum: ["SCHEDULED", "IN_PROGRESS", "COMPLETED", "CANCELLED"] },
        groupId: { bsonType: ["long", "null"] },
        round: { bsonType: ["string", "null"] }
      }
    }
  },
  counters: {
    $jsonSchema: {
      bsonType: "object",
      required: ["_id", "value"],
      properties: { _id: { bsonType: "string" }, value: { bsonType: "long", minimum: 0 } }
    }
  }
};

for (const [collectionName, validator] of Object.entries(validators)) {
  const exists = appdb.getCollectionNames().includes(collectionName);
  if (exists) {
    appdb.runCommand({ collMod: collectionName, validator, validationLevel: "strict", validationAction: "error" });
  } else {
    appdb.createCollection(collectionName, { validator, validationLevel: "strict", validationAction: "error" });
  }
}

appdb.players.createIndex({ nickname: 1 }, { unique: true });
appdb.teams.createIndex({ name: 1 }, { unique: true });
appdb.tournaments.createIndex({ name: 1 }, { unique: true });
appdb.groups.createIndex({ tournamentId: 1 });
appdb.matches.createIndex({ groupId: 1 });

const players = [
  { _id: NumberLong("1"), type: "PRO", nickname: "sample-awper", role: "AWPer", salary: NumberDecimal("5000.00"), sponsor: "Sample Sponsor" },
  { _id: NumberLong("2"), type: "PRO", nickname: "sample-rifler", role: "Rifler", salary: NumberDecimal("4000.00"), sponsor: "Sample Sponsor" },
  { _id: NumberLong("3"), type: "AMATEUR", nickname: "sample-support", role: "Support", rank: "Global Elite", hoursPlayed: 1200 },
  { _id: NumberLong("4"), type: "PRO", nickname: "sample-awper-2", role: "AWPer", salary: NumberDecimal("4800.00"), sponsor: "Sample Sponsor" },
  { _id: NumberLong("5"), type: "PRO", nickname: "sample-rifler-2", role: "Rifler", salary: NumberDecimal("3900.00"), sponsor: "Sample Sponsor" },
  { _id: NumberLong("6"), type: "AMATEUR", nickname: "sample-support-2", role: "Support", rank: "Supreme", hoursPlayed: 900 }
];
for (const player of players) appdb.players.replaceOne({ _id: player._id }, player, { upsert: true });

appdb.teams.replaceOne({ _id: NumberLong("1") }, {
  _id: NumberLong("1"), name: "Sample Team Alpha", region: "EU",
  playerIds: [NumberLong("1"), NumberLong("2"), NumberLong("3")]
}, { upsert: true });
appdb.teams.replaceOne({ _id: NumberLong("2") }, {
  _id: NumberLong("2"), name: "Sample Team Beta", region: "NA",
  playerIds: [NumberLong("4"), NumberLong("5"), NumberLong("6")]
}, { upsert: true });

appdb.tournaments.replaceOne({ _id: NumberLong("1") }, {
  _id: NumberLong("1"), type: "ONLINE", name: "Sample Online Cup",
  startDate: ISODate("2026-11-06T00:00:00Z"), endDate: ISODate("2026-11-08T00:00:00Z"),
  prizePool: NumberDecimal("10000.00"), status: "REGISTRATION_OPEN", maxTeams: 8,
  registeredTeamIds: [NumberLong("1"), NumberLong("2")],
  streamUrl: "https://example.com/stream", serverLocation: "EU-West"
}, { upsert: true });

appdb.groups.replaceOne({ _id: NumberLong("1") }, {
  _id: NumberLong("1"), name: "Group A", tournamentId: NumberLong("1"),
  teamIds: [NumberLong("1"), NumberLong("2")]
}, { upsert: true });

appdb.matches.replaceOne({ _id: NumberLong("1") }, {
  _id: NumberLong("1"), team1Id: NumberLong("1"), team2Id: NumberLong("2"),
  team1Score: 0, team2Score: 0, matchDate: ISODate("2026-11-07T14:00:00Z"),
  status: "SCHEDULED", groupId: NumberLong("1"), round: "Group Stage"
}, { upsert: true });

const counters = appdb.getCollection("counters");
for (const [sequence, value] of [["players", 6], ["teams", 2], ["tournaments", 1], ["groups", 1], ["matches", 1]]) {
  counters.updateOne({ _id: sequence }, { $max: { value: NumberLong(String(value)) } }, { upsert: true });
}

print("Replica set rs0 is ready; esportsdb has indexes and sample data.");

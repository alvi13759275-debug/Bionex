const { initializeTestEnvironment, assertFails, assertSucceeds } = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

const PROJECT_ID = "demo-no-project";
const DATABASE_ID = "ai-studio-android-bionexus-5257f4ed-3252-46d8-ba27-9ca4d66d543a";

let testEnv;

before(async () => {
  const rules = fs.readFileSync("firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: "127.0.0.1",
      port: 8085,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user cannot read or write projects", async () => {
  const unauthedDb = testEnv.unauthenticatedContext().firestore();
  const docRef = unauthedDb.collection("users").doc("userA").collection("projects").doc("proj1");
  await assertFails(docRef.get());
  await assertFails(docRef.set({ title: "Test" }));
});

test("Authenticated user can create valid project under their own path", async () => {
  const aliceDb = testEnv.authenticatedContext("alice").firestore();
  const docRef = aliceDb.collection("users").doc("alice").collection("projects").doc("proj1");
  
  await assertSucceeds(
    docRef.set({
      userId: "alice",
      title: "CRISPR-Cas9 Off-Target Analysis",
      category: "Genomics",
      summary: "High-throughput sequencing analysis of gRNA off-target cut sites.",
      content: "Details regarding guide RNA design and validation.",
      tags: ["CRISPR", "Genomics", "DNA"],
      createdAt: new Date(),
      updatedAt: new Date()
    })
  );
});

test("Authenticated user cannot access another user's project", async () => {
  // Alice creates her project via admin
  await testEnv.withSecurityRulesDisabled(async (context) => {
    const adminDb = context.firestore();
    await adminDb.collection("users").doc("alice").collection("projects").doc("proj1").set({
      userId: "alice",
      title: "Private Research",
      category: "Genomics",
      summary: "Confidential genome findings.",
      createdAt: new Date(),
      updatedAt: new Date()
    });
  });

  const bobDb = testEnv.authenticatedContext("bob").firestore();
  const aliceDocRefFromBob = bobDb.collection("users").doc("alice").collection("projects").doc("proj1");
  
  await assertFails(aliceDocRefFromBob.get());
  await assertFails(aliceDocRefFromBob.set({ title: "Hacked" }));
});

test("Authenticated user can create valid protocol", async () => {
  const aliceDb = testEnv.authenticatedContext("alice").firestore();
  const docRef = aliceDb.collection("users").doc("alice").collection("protocols").doc("proto1");

  await assertSucceeds(
    docRef.set({
      userId: "alice",
      name: "Western Blot SDS-PAGE Protocol",
      category: "Proteomics",
      reagents: ["Tris-HCl", "SDS", "Polyacrylamide", "TBS-T"],
      steps: ["Prepare gel", "Load protein lysate", "Run 120V 90m", "Transfer to PVDF"],
      safetyNotes: "Wear gloves and eye protection when handling acrylamide (neurotoxin).",
      status: "Active",
      createdAt: new Date(),
      updatedAt: new Date()
    })
  );
});

test("Invalid project payload is rejected", async () => {
  const aliceDb = testEnv.authenticatedContext("alice").firestore();
  const docRef = aliceDb.collection("users").doc("alice").collection("projects").doc("proj_invalid");

  // Missing required summary & category
  await assertFails(
    docRef.set({
      userId: "alice",
      title: "Incomplete Project",
      createdAt: new Date(),
      updatedAt: new Date()
    })
  );
});

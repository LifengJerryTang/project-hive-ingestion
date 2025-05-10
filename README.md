# 🐝 Project Hive — Ingestion Layer

> **Modern, event-driven ingestion platform built on AWS CDK and Java**  
> Ingests Gmail and Discord data, handles OAuth2, and publishes events into a scalable event bus architecture.

---

## 📌 Overview

This repository implements the **ingestion layer** of [Project Hive](https://docs.google.com/document/d/1FfHPNgEEgrdEjyCs-si1gWF8x90gF_r7Bemp6vooX10/edit?tab=t.0), a multi-account, serverless platform for daily message aggregation and summarization using large language models (LLMs).

Project Hive helps users stay informed by aggregating their fragmented communications — from email threads to Discord channels — into a single daily digest powered by AI.

All infrastructure in this repository is deployed into a **dedicated AWS account focused exclusively on secure, scheduled data ingestion**.

---

## 📄 Design Document

For an in-depth look at the system design, architecture decisions, and technical tradeoffs, view the Project Hive design doc:

👉 [Project Hive Design Doc](https://docs.google.com/document/d/1FfHPNgEEgrdEjyCs-si1gWF8x90gF_r7Bemp6vooX10/edit?tab=t.0)

---

## 🧱 Role of This Repo in the Full System

This ingestion layer is the entire platform's **data entry point**. Its key responsibilities include:

- **Polling Gmail and Discord at regular intervals** to collect the latest user messages
- **Handling OAuth2 flows securely** with token storage and refresh via AWS Secrets Manager
- **Normalizing and publishing message events** into Amazon EventBridge for downstream processing
- **Decoupling ingestion from summarization**, enabling horizontal scaling and cross-account event delivery

Once published, these events are consumed by LLM-backed summarization services in another AWS account, which then produce digest summaries and deliver them to end users via a UI layer.

---


## 📂 Tech Stack

| Layer               | Technology             |
|--------------------|------------------------|
| Infrastructure     | AWS CDK (TypeScript)   |
| Lambdas            | Java 17 + Maven        |
| Scheduling         | Amazon EventBridge     |
| Secrets Management | AWS Secrets Manager    |
| Event Routing      | Amazon EventBridge     |

---

## 📁 Monorepo Structure
```
project-hive-ingestion/
├── lib/ # CDK stacks for Lambdas, OAuth, EventBridge
├── bin/ # CDK app entry point
├── artifacts/ # Built Lambda .jar files
├── gmail-ingestion/ # Java package for Gmail Lambda
├── discord-ingestion/ # Java package for Discord Lambda
├── oauth-redirect-handler/ # Java package for OAuth redirect Lambda
```

---

## 🚀 Features

- **Event-Driven Design**  
  EventBridge routes structured messages from ingestion Lambdas to downstream consumers.

- **OAuth2 Integration**  
  Secure, token-based authorization with Gmail and Discord.

- **Modular Java Lambdas**  
  Each function is isolated, testable, and deployed via .jar artifacts.

- **Cross-Account Compatible**  
  Events can be routed to a separate AWS account that handles the control plane and presentation layer.

---

## 🧪 Getting Started

> 🛑 _This repo assumes you are deploying into the ingestion-focused AWS account._  
> 🔧 _Java and Node.js (for CDK) must be installed._

### 1. Clone the Repo

```
git clone https://github.com/<your-username>/project-hive-ingestion.git
cd project-hive-ingestion
```

### 2. Build Lambda Artifacts
```
cd gmail-ingestion && mvn clean package && cd ..
cp gmail-ingestion/target/*.jar artifacts/
```

### 3. Deploy CDK Stacks
```
npm install
cdk deploy
```

## 📜 License
[MIT License](https://github.com/LifengJerryTang/project-hive-ingestion/blob/main/LICENSE)

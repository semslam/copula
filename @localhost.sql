CREATE TABLE IF NOT EXISTS copula.userProfile (
  userId         INTEGER PRIMARY KEY AUTO_INCREMENT,
  username       VARCHAR(50) NOT NULL,
  password       TEXT,
  email          VARCHAR(100),
  bio            VARCHAR(200),
  feedPostCount  INTEGER(10)         DEFAULT 0,
  followerCount  INTEGER(10)         DEFAULT 0,
  followingCount INTEGER(10)         DEFAULT 0,
  pnToken        TEXT,
  accountType    INTEGER(1),
  tpUserId       VARCHAR(30),
  createdAt      DATETIME,
  updatedAt      DATETIME
);

CREATE TABLE IF NOT EXISTS copula.feedLikeRegister (
  _id       INTEGER PRIMARY KEY  AUTO_INCREMENT,
  feedId    VARCHAR(30),
  userId    VARCHAR(30),
  createdAt DATETIME
);

CREATE TABLE IF NOT EXISTS copula.feedBucket (
  feedId       INTEGER PRIMARY KEY  AUTO_INCREMENT,
  posterId     VARCHAR(30),
  feedType     INTEGER(2),
  mediaCaption VARCHAR(300),
  mediaTitle   VARCHAR(100),
  dataUrl      VARCHAR(200),
  streamUrl    VARCHAR(200),
  thumbnailUrl VARCHAR(200),
  mediaType    INTEGER(2),
  likeCount    INTEGER(10)          DEFAULT 0,
  tag          TEXT,
  mediaMeta    TEXT,
  createdAt    DATETIME,
  updatedAt    DATETIME
);

CREATE TABLE IF NOT EXISTS copula.userContent (
  _id       INTEGER PRIMARY KEY  AUTO_INCREMENT,
  userId    VARCHAR(30),
  typeId    INTEGER(3),
  contentId VARCHAR(30),
  createdAt DATETIME
);

CREATE TABLE IF NOT EXISTS copula.followerLine (
  _id    INTEGER PRIMARY KEY  AUTO_INCREMENT,
  userId VARCHAR(30),
  feedId VARCHAR(30)
);

CREATE TABLE IF NOT EXISTS copula.userFollow (
  _id          INTEGER PRIMARY KEY AUTO_INCREMENT,
  userId       VARCHAR(30),
  followUserId VARCHAR(30),
  createdAt    DATETIME
);


CREATE TABLE IF NOT EXISTS copula.userFeedback (
  _id       INTEGER PRIMARY KEY AUTO_INCREMENT,
  deviceId  VARCHAR(30),
  email     VARCHAR(100),
  feature   VARCHAR(100),
  message   VARCHAR(100),
  createdAt DATETIME
);

CREATE TABLE IF NOT EXISTS copula.deviceFeedback (
  _id       INTEGER PRIMARY KEY AUTO_INCREMENT,
  email     VARCHAR(100),
  deviceId  VARCHAR(100),
  message   VARCHAR(500),
  createdAt DATETIME
);

CREATE TABLE IF NOT EXISTS copula.adminAccount (
  username VARCHAR(30) PRIMARY KEY,
  password VARCHAR(200)
);

CREATE TABLE IF NOT EXISTS copula.apiAuthToken (
  username  VARCHAR(30) PRIMARY KEY,
  apiToken  VARCHAR(500),
  expiresOn DATETIME
);

CREATE TABLE IF NOT EXISTS copula.configuration (
  configKey   VARCHAR(30) PRIMARY KEY,
  configValue VARCHAR(100),
  configType  VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS copula.userInterest (
  userId   VARCHAR(30) PRIMARY KEY,
  interest TEXT
);

CREATE INDEX idx_followUserId
  ON userFollow (followUserId, userId);

CREATE INDEX idx_feedType
  ON feedBucket (feedType);

CREATE INDEX idx_userIdFeedId
  ON feedLikeRegister (userId, feedId);
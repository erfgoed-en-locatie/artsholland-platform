INSERT INTO role (id, role)
    VALUES (1, 'ROLE_API_USER');

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

INSERT INTO apiuser (email)
    VALUES ('artsholland@waag.org');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('1e4263ef2d20da8eff6996381bb0d78b', (SELECT id FROM apiuser WHERE email = 'artsholland@waag.org'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('drupal-admin@waag.org');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('9cbce178ed121b61a0797500d62cd440', (SELECT id FROM apiuser WHERE email = 'drupal-admin@waag.org'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('edwin@mobidapt.com');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('fa0f3f26206b362384a323b40e40e1e6', (SELECT id FROM apiuser WHERE email = 'edwin@mobidapt.com'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('lex@wikiwise.nl');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('2f48b965c75f7c5e5e6780b3b74ada4b', (SELECT id FROM apiuser WHERE email = 'lex@wikiwise.nl'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('hidde.braun@gmail.com');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('b2b397b9b80fc3f0dc689ba6643f2530', (SELECT id FROM apiuser WHERE email = 'hidde.braun@gmail.com'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('rvtwestende@totalactivemedia.nl');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('cd9ef19f1993165580cf026734dfa664', (SELECT id FROM apiuser WHERE email = 'rvtwestende@totalactivemedia.nl'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('tom@waag.org');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('85715d4734ee8a22571c6b69a789d8ac', (SELECT id FROM apiuser WHERE email = 'tom@waag.org'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('info@martijnmellema.com');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('27b6abd4129fc9ced3aa5390fd4fb15b', (SELECT id FROM apiuser WHERE email = 'info@martijnmellema.com'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('bert@waag.org');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('12f1555e827797cd6a3e84f563919666', (SELECT id FROM apiuser WHERE email = 'bert@waag.org'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('jacco@in10.nl');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('a4cb090815566d01a8963aa2372bccdc', (SELECT id FROM apiuser WHERE email = 'jacco@in10.nl'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('puck@pickipedia.nl');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('a73b9392cd470b9ffa44525f610d4010', (SELECT id FROM apiuser WHERE email = 'puck@pickipedia.nl'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('info@adriaan.tv');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('c5e9b013eea96c4ab3ba3604d6b603ff', (SELECT id FROM apiuser WHERE email = 'info@adriaan.tv'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('paul@glimworm.com');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('6028db1793efa268928016a075c67d8f', (SELECT id FROM apiuser WHERE email = 'paul@glimworm.com'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('bouwmanmark@gmail.com');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('693af12bbe3c807a7d939d25a47ce8f1', (SELECT id FROM apiuser WHERE email = 'bouwmanmark@gmail.com'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('maarten.kroon@armatiek.nl');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('800da0b89bc36964a52bd9f5af1b0b1b', (SELECT id FROM apiuser WHERE email = 'maarten.kroon@armatiek.nl'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('francisca@omnipmedia.com');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('4216387078e091890ba35e5190deabad', (SELECT id FROM apiuser WHERE email = 'francisca@omnipmedia.com'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('hubertinebergsma@gmail.com');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('52c4ec1f9a78b26e70175ceadcb24d90', (SELECT id FROM apiuser WHERE email = 'hubertinebergsma@gmail.com'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('tijs@automatique.nl');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('0b770c8c3065cb177a7e2da879ce04ac', (SELECT id FROM apiuser WHERE email = 'tijs@automatique.nl'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('ego@fr4ncis.net');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('d9b9e3e05d77dc51b6e8cb4b0e645118', (SELECT id FROM apiuser WHERE email = 'ego@fr4ncis.net'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('kioli85@gmail.com');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('cd63e58821ca5e43d6dc6ff2c8a59df8', (SELECT id FROM apiuser WHERE email = 'kioli85@gmail.com'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('pizzichemi.marco@gmail.com');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('ec7821f098274c89bc72df6d1737d6dc', (SELECT id FROM apiuser WHERE email = 'pizzichemi.marco@gmail.com'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('f.vandenboom@hotmail.com');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('a8ae4a4df194124a0e09dda7c9b1ce7d', (SELECT id FROM apiuser WHERE email = 'f.vandenboom@hotmail.com'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('jim@competa.com');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('b2dc710c87ec1431700c6c1a13648b5b', (SELECT id FROM apiuser WHERE email = 'jim@competa.com'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('chris@databol.nl');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('6cd744c4bc4e33bfd1c08380ea203852', (SELECT id FROM apiuser WHERE email = 'chris@databol.nl'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('sybrenbouwsma@gmail.com');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('5090ffc7e9cb40c5522bac87b7a18985', (SELECT id FROM apiuser WHERE email = 'sybrenbouwsma@gmail.com'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('dezeruimte@gmail.com');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('bd0d3400ffae6b4e67e0de992bb3fb5e', (SELECT id FROM apiuser WHERE email = 'dezeruimte@gmail.com'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('info@hiscue.com');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('e51d513969424cd4f3625223b24b7f50', (SELECT id FROM apiuser WHERE email = 'info@hiscue.com'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('info@erikdejager.nl');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('227aba3b0f584e77f4c452820bf43714', (SELECT id FROM apiuser WHERE email = 'info@erikdejager.nl'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('milantoet@gmail.com');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('9be8a1e61d760c711a2c4a24bbcfcd12', (SELECT id FROM apiuser WHERE email = 'milantoet@gmail.com'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('l.r.coppejans@gmail.com');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('1d54a69e7cbecb727cb4e2c30514d021', (SELECT id FROM apiuser WHERE email = 'l.r.coppejans@gmail.com'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('andre@toly.nl');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('bb23ab70bb6252d88c03be88c5d24797', (SELECT id FROM apiuser WHERE email = 'andre@toly.nl'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('marcelvanmackelenbergh@gmail.com');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('24144c51240062eed4238ddda18ff10c', (SELECT id FROM apiuser WHERE email = 'marcelvanmackelenbergh@gmail.com'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('info@rays.nl');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('37545c7eb6211caf66425062ae14ff4c', (SELECT id FROM apiuser WHERE email = 'info@rays.nl'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('liesbeth.keijser@nationaalarchief.nl');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('5f81e0e56e16fe9d566fcb39057ac489', (SELECT id FROM apiuser WHERE email = 'liesbeth.keijser@nationaalarchief.nl'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('sander@pixelgilde.nl');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('5a3d656435b5efb3d55f989ac83fd752', (SELECT id FROM apiuser WHERE email = 'sander@pixelgilde.nl'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('Hans.nouwens@Openarchief.org');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('0c5ce6abf1f8595ef8b467e9eae60edc', (SELECT id FROM apiuser WHERE email = 'Hans.nouwens@Openarchief.org'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('hackaton@manenschijn.com');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('e77d888ebb4abffcbf4df57438adfc6d', (SELECT id FROM apiuser WHERE email = 'hackaton@manenschijn.com'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('riknijessen@hotmail.com');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('424205571e5a217380408ebe293c353f', (SELECT id FROM apiuser WHERE email = 'riknijessen@hotmail.com'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('jensw@ch.tudelft.nl');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('bb69f6847e3e9ef3de3facf79488134b', (SELECT id FROM apiuser WHERE email = 'jensw@ch.tudelft.nl'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('nsl.van.ee@gmail.com');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('5f1472c203baa2a5c293a7c72c2c49f0', (SELECT id FROM apiuser WHERE email = 'nsl.van.ee@gmail.com'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('robertmassa@gmail.com');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('0819ccaea3cce944cfd1a9d10c52e63d', (SELECT id FROM apiuser WHERE email = 'robertmassa@gmail.com'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('m.hildebrand@vu.nl');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('3e8f5eae20488dd5d7cd56472ce7e582', (SELECT id FROM apiuser WHERE email = 'm.hildebrand@vu.nl'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('rogier.peters@gmail.com');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('4300ed350b19f9b2962cf7b07a596816', (SELECT id FROM apiuser WHERE email = 'rogier.peters@gmail.com'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('milanmaurice@gmail.com');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('8fe394811438f6d2bef7637a8ae8085c', (SELECT id FROM apiuser WHERE email = 'milanmaurice@gmail.com'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('dirklectisch@gmail.com');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('f8895d4beccf786fa21466bb9ef4f84b', (SELECT id FROM apiuser WHERE email = 'dirklectisch@gmail.com'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('remko@waag.org');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('325a8441cd2ef326c0513b95dec9bc72', (SELECT id FROM apiuser WHERE email = 'remko@waag.org'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('bsighem@centraalmuseum.nl');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('ab16b93dd2bb413b59b211ffbb703c5f', (SELECT id FROM apiuser WHERE email = 'bsighem@centraalmuseum.nl'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('simeon@ndkv.nl');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('1ce19d3a962910586693ebd35bce7cee', (SELECT id FROM apiuser WHERE email = 'simeon@ndkv.nl'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('bjorn@exto.nl');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('9cc056ae16229927d937624420381150', (SELECT id FROM apiuser WHERE email = 'bjorn@exto.nl'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('jon@movingroot.com');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('5f093585a10a0fd1c131e22a224a9b3d', (SELECT id FROM apiuser WHERE email = 'jon@movingroot.com'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('jesusgollonet@gmail.com');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('2152d1cad079bca56dfdb00e312ad928', (SELECT id FROM apiuser WHERE email = 'jesusgollonet@gmail.com'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('eddieschoute@gmail.com');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('73277fc97f0df807895910bfbac5dcc0', (SELECT id FROM apiuser WHERE email = 'eddieschoute@gmail.com'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('jcapka@xcomplica.com');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('bde04cacce3e7b4339c48b18d2b6af36', (SELECT id FROM apiuser WHERE email = 'jcapka@xcomplica.com'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
INSERT INTO apiuser (email)
    VALUES ('gaby.verdouw@geevadvies.nl');

INSERT INTO app (apikey, apiuser_id, role_id)
    VALUES ('32001e2efdfd438bf68e8ba65f69966c', (SELECT id FROM apiuser WHERE email = 'gaby.verdouw@geevadvies.nl'), 1);

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
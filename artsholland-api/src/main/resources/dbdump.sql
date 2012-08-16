--
-- PostgreSQL database dump
--

-- Dumped from database version 9.1.4
-- Dumped by pg_dump version 9.1.3
-- Started on 2012-08-16 14:58:37 CEST

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 167 (class 1259 OID 64238)
-- Dependencies: 2754 5
-- Name: apiuser; Type: TABLE; Schema: public; Owner: artsholland; Tablespace: 
--

CREATE TABLE apiuser (
    id integer NOT NULL,
    email text NOT NULL,
    name text,
    created timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.apiuser OWNER TO artsholland;

--
-- TOC entry 166 (class 1259 OID 64236)
-- Dependencies: 5 167
-- Name: apiuser_id_seq; Type: SEQUENCE; Schema: public; Owner: artsholland
--

CREATE SEQUENCE apiuser_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.apiuser_id_seq OWNER TO artsholland;

--
-- TOC entry 2775 (class 0 OID 0)
-- Dependencies: 166
-- Name: apiuser_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: artsholland
--

ALTER SEQUENCE apiuser_id_seq OWNED BY apiuser.id;


--
-- TOC entry 2776 (class 0 OID 0)
-- Dependencies: 166
-- Name: apiuser_id_seq; Type: SEQUENCE SET; Schema: public; Owner: artsholland
--

SELECT pg_catalog.setval('apiuser_id_seq', 55, true);


--
-- TOC entry 171 (class 1259 OID 64277)
-- Dependencies: 2757 5
-- Name: app; Type: TABLE; Schema: public; Owner: artsholland; Tablespace: 
--

CREATE TABLE app (
    id integer NOT NULL,
    apikey text,
    apiuser_id integer,
    secret text,
    name text,
    title text,
    url text,
    description text,
    created timestamp without time zone DEFAULT now() NOT NULL,
    role_id integer NOT NULL
);


ALTER TABLE public.app OWNER TO artsholland;

--
-- TOC entry 170 (class 1259 OID 64275)
-- Dependencies: 171 5
-- Name: app_id_seq; Type: SEQUENCE; Schema: public; Owner: artsholland
--

CREATE SEQUENCE app_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.app_id_seq OWNER TO artsholland;

--
-- TOC entry 2777 (class 0 OID 0)
-- Dependencies: 170
-- Name: app_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: artsholland
--

ALTER SEQUENCE app_id_seq OWNED BY app.id;


--
-- TOC entry 2778 (class 0 OID 0)
-- Dependencies: 170
-- Name: app_id_seq; Type: SEQUENCE SET; Schema: public; Owner: artsholland
--

SELECT pg_catalog.setval('app_id_seq', 55, true);


--
-- TOC entry 169 (class 1259 OID 64264)
-- Dependencies: 5
-- Name: role; Type: TABLE; Schema: public; Owner: artsholland; Tablespace: 
--

CREATE TABLE role (
    id integer NOT NULL,
    role text NOT NULL
);


ALTER TABLE public.role OWNER TO artsholland;

--
-- TOC entry 168 (class 1259 OID 64262)
-- Dependencies: 169 5
-- Name: role_id_seq; Type: SEQUENCE; Schema: public; Owner: artsholland
--

CREATE SEQUENCE role_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.role_id_seq OWNER TO artsholland;

--
-- TOC entry 2779 (class 0 OID 0)
-- Dependencies: 168
-- Name: role_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: artsholland
--

ALTER SEQUENCE role_id_seq OWNED BY role.id;


--
-- TOC entry 2780 (class 0 OID 0)
-- Dependencies: 168
-- Name: role_id_seq; Type: SEQUENCE SET; Schema: public; Owner: artsholland
--

SELECT pg_catalog.setval('role_id_seq', 1, false);


--
-- TOC entry 2753 (class 2604 OID 64241)
-- Dependencies: 166 167 167
-- Name: id; Type: DEFAULT; Schema: public; Owner: artsholland
--

ALTER TABLE ONLY apiuser ALTER COLUMN id SET DEFAULT nextval('apiuser_id_seq'::regclass);


--
-- TOC entry 2756 (class 2604 OID 64280)
-- Dependencies: 170 171 171
-- Name: id; Type: DEFAULT; Schema: public; Owner: artsholland
--

ALTER TABLE ONLY app ALTER COLUMN id SET DEFAULT nextval('app_id_seq'::regclass);


--
-- TOC entry 2755 (class 2604 OID 64267)
-- Dependencies: 168 169 169
-- Name: id; Type: DEFAULT; Schema: public; Owner: artsholland
--

ALTER TABLE ONLY role ALTER COLUMN id SET DEFAULT nextval('role_id_seq'::regclass);


--
-- TOC entry 2770 (class 0 OID 64238)
-- Dependencies: 167
-- Data for Name: apiuser; Type: TABLE DATA; Schema: public; Owner: artsholland
--

COPY apiuser (id, email, name, created) FROM stdin;
2	artsholland@waag.org	\N	2012-08-16 14:52:13.915233
3	drupal-admin@waag.org	\N	2012-08-16 14:52:13.915233
4	edwin@mobidapt.com	\N	2012-08-16 14:52:13.915233
5	lex@wikiwise.nl	\N	2012-08-16 14:52:13.915233
6	hidde.braun@gmail.com	\N	2012-08-16 14:52:13.915233
7	rvtwestende@totalactivemedia.nl	\N	2012-08-16 14:52:13.915233
8	tom@waag.org	\N	2012-08-16 14:52:13.915233
9	info@martijnmellema.com	\N	2012-08-16 14:52:13.915233
10	bert@waag.org	\N	2012-08-16 14:52:13.915233
11	jacco@in10.nl	\N	2012-08-16 14:52:13.915233
12	puck@pickipedia.nl	\N	2012-08-16 14:52:13.915233
13	info@adriaan.tv	\N	2012-08-16 14:52:13.915233
14	paul@glimworm.com	\N	2012-08-16 14:52:13.915233
15	bouwmanmark@gmail.com	\N	2012-08-16 14:52:13.915233
16	maarten.kroon@armatiek.nl	\N	2012-08-16 14:52:13.915233
17	francisca@omnipmedia.com	\N	2012-08-16 14:52:13.915233
18	hubertinebergsma@gmail.com	\N	2012-08-16 14:52:13.915233
19	tijs@automatique.nl	\N	2012-08-16 14:52:13.915233
20	ego@fr4ncis.net	\N	2012-08-16 14:52:13.915233
21	kioli85@gmail.com	\N	2012-08-16 14:52:13.915233
22	pizzichemi.marco@gmail.com	\N	2012-08-16 14:52:13.915233
23	f.vandenboom@hotmail.com	\N	2012-08-16 14:52:13.915233
24	jim@competa.com	\N	2012-08-16 14:52:13.915233
25	chris@databol.nl	\N	2012-08-16 14:52:13.915233
26	sybrenbouwsma@gmail.com	\N	2012-08-16 14:52:13.915233
27	dezeruimte@gmail.com	\N	2012-08-16 14:52:13.915233
28	info@hiscue.com	\N	2012-08-16 14:52:13.915233
29	info@erikdejager.nl	\N	2012-08-16 14:52:13.915233
30	milantoet@gmail.com	\N	2012-08-16 14:52:13.915233
31	l.r.coppejans@gmail.com	\N	2012-08-16 14:52:13.915233
32	andre@toly.nl	\N	2012-08-16 14:52:13.915233
33	marcelvanmackelenbergh@gmail.com	\N	2012-08-16 14:52:13.915233
34	info@rays.nl	\N	2012-08-16 14:52:13.915233
35	liesbeth.keijser@nationaalarchief.nl	\N	2012-08-16 14:52:13.915233
36	sander@pixelgilde.nl	\N	2012-08-16 14:52:13.915233
37	Hans.nouwens@Openarchief.org	\N	2012-08-16 14:52:13.915233
38	hackaton@manenschijn.com	\N	2012-08-16 14:52:13.915233
39	riknijessen@hotmail.com	\N	2012-08-16 14:52:13.915233
40	jensw@ch.tudelft.nl	\N	2012-08-16 14:52:13.915233
41	nsl.van.ee@gmail.com	\N	2012-08-16 14:52:13.915233
42	robertmassa@gmail.com	\N	2012-08-16 14:52:13.915233
43	m.hildebrand@vu.nl	\N	2012-08-16 14:52:13.915233
44	rogier.peters@gmail.com	\N	2012-08-16 14:52:13.915233
45	milanmaurice@gmail.com	\N	2012-08-16 14:52:13.915233
46	dirklectisch@gmail.com	\N	2012-08-16 14:52:13.915233
47	remko@waag.org	\N	2012-08-16 14:52:13.915233
48	bsighem@centraalmuseum.nl	\N	2012-08-16 14:52:13.915233
49	simeon@ndkv.nl	\N	2012-08-16 14:52:13.915233
50	bjorn@exto.nl	\N	2012-08-16 14:52:13.915233
51	jon@movingroot.com	\N	2012-08-16 14:52:13.915233
52	jesusgollonet@gmail.com	\N	2012-08-16 14:52:13.915233
53	eddieschoute@gmail.com	\N	2012-08-16 14:52:13.915233
54	jcapka@xcomplica.com	\N	2012-08-16 14:52:13.915233
55	gaby.verdouw@geevadvies.nl	\N	2012-08-16 14:52:13.915233
\.


--
-- TOC entry 2772 (class 0 OID 64277)
-- Dependencies: 171
-- Data for Name: app; Type: TABLE DATA; Schema: public; Owner: artsholland
--

COPY app (id, apikey, apiuser_id, secret, name, title, url, description, created, role_id) FROM stdin;
2	1e4263ef2d20da8eff6996381bb0d78b	2	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
3	9cbce178ed121b61a0797500d62cd440	3	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
4	fa0f3f26206b362384a323b40e40e1e6	4	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
5	2f48b965c75f7c5e5e6780b3b74ada4b	5	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
6	b2b397b9b80fc3f0dc689ba6643f2530	6	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
7	cd9ef19f1993165580cf026734dfa664	7	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
8	85715d4734ee8a22571c6b69a789d8ac	8	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
9	27b6abd4129fc9ced3aa5390fd4fb15b	9	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
10	12f1555e827797cd6a3e84f563919666	10	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
11	a4cb090815566d01a8963aa2372bccdc	11	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
12	a73b9392cd470b9ffa44525f610d4010	12	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
13	c5e9b013eea96c4ab3ba3604d6b603ff	13	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
14	6028db1793efa268928016a075c67d8f	14	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
15	693af12bbe3c807a7d939d25a47ce8f1	15	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
16	800da0b89bc36964a52bd9f5af1b0b1b	16	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
17	4216387078e091890ba35e5190deabad	17	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
18	52c4ec1f9a78b26e70175ceadcb24d90	18	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
19	0b770c8c3065cb177a7e2da879ce04ac	19	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
20	d9b9e3e05d77dc51b6e8cb4b0e645118	20	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
21	cd63e58821ca5e43d6dc6ff2c8a59df8	21	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
22	ec7821f098274c89bc72df6d1737d6dc	22	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
23	a8ae4a4df194124a0e09dda7c9b1ce7d	23	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
24	b2dc710c87ec1431700c6c1a13648b5b	24	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
25	6cd744c4bc4e33bfd1c08380ea203852	25	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
26	5090ffc7e9cb40c5522bac87b7a18985	26	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
27	bd0d3400ffae6b4e67e0de992bb3fb5e	27	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
28	e51d513969424cd4f3625223b24b7f50	28	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
29	227aba3b0f584e77f4c452820bf43714	29	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
30	9be8a1e61d760c711a2c4a24bbcfcd12	30	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
31	1d54a69e7cbecb727cb4e2c30514d021	31	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
32	bb23ab70bb6252d88c03be88c5d24797	32	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
33	24144c51240062eed4238ddda18ff10c	33	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
34	37545c7eb6211caf66425062ae14ff4c	34	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
35	5f81e0e56e16fe9d566fcb39057ac489	35	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
36	5a3d656435b5efb3d55f989ac83fd752	36	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
37	0c5ce6abf1f8595ef8b467e9eae60edc	37	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
38	e77d888ebb4abffcbf4df57438adfc6d	38	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
39	424205571e5a217380408ebe293c353f	39	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
40	bb69f6847e3e9ef3de3facf79488134b	40	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
41	5f1472c203baa2a5c293a7c72c2c49f0	41	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
42	0819ccaea3cce944cfd1a9d10c52e63d	42	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
43	3e8f5eae20488dd5d7cd56472ce7e582	43	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
44	4300ed350b19f9b2962cf7b07a596816	44	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
45	8fe394811438f6d2bef7637a8ae8085c	45	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
46	f8895d4beccf786fa21466bb9ef4f84b	46	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
47	325a8441cd2ef326c0513b95dec9bc72	47	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
48	ab16b93dd2bb413b59b211ffbb703c5f	48	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
49	1ce19d3a962910586693ebd35bce7cee	49	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
50	9cc056ae16229927d937624420381150	50	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
51	5f093585a10a0fd1c131e22a224a9b3d	51	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
52	2152d1cad079bca56dfdb00e312ad928	52	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
53	73277fc97f0df807895910bfbac5dcc0	53	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
54	bde04cacce3e7b4339c48b18d2b6af36	54	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
55	32001e2efdfd438bf68e8ba65f69966c	55	\N	\N	\N	\N	\N	2012-08-16 14:52:13.915233	1
\.


--
-- TOC entry 2771 (class 0 OID 64264)
-- Dependencies: 169
-- Data for Name: role; Type: TABLE DATA; Schema: public; Owner: artsholland
--

COPY role (id, role) FROM stdin;
1	ROLE_API_USER
2	ROLE_PLATFORM_ADMIN
\.


--
-- TOC entry 2759 (class 2606 OID 64249)
-- Dependencies: 167 167
-- Name: apiuser_email_key; Type: CONSTRAINT; Schema: public; Owner: artsholland; Tablespace: 
--

ALTER TABLE ONLY apiuser
    ADD CONSTRAINT apiuser_email_key UNIQUE (email);


--
-- TOC entry 2761 (class 2606 OID 64247)
-- Dependencies: 167 167
-- Name: pk_apiuser_id; Type: CONSTRAINT; Schema: public; Owner: artsholland; Tablespace: 
--

ALTER TABLE ONLY apiuser
    ADD CONSTRAINT pk_apiuser_id PRIMARY KEY (id);


--
-- TOC entry 2767 (class 2606 OID 64286)
-- Dependencies: 171 171
-- Name: pk_app_id; Type: CONSTRAINT; Schema: public; Owner: artsholland; Tablespace: 
--

ALTER TABLE ONLY app
    ADD CONSTRAINT pk_app_id PRIMARY KEY (id);


--
-- TOC entry 2763 (class 2606 OID 64272)
-- Dependencies: 169 169
-- Name: pk_role_id; Type: CONSTRAINT; Schema: public; Owner: artsholland; Tablespace: 
--

ALTER TABLE ONLY role
    ADD CONSTRAINT pk_role_id PRIMARY KEY (id);


--
-- TOC entry 2765 (class 2606 OID 64274)
-- Dependencies: 169 169
-- Name: role_role_key; Type: CONSTRAINT; Schema: public; Owner: artsholland; Tablespace: 
--

ALTER TABLE ONLY role
    ADD CONSTRAINT role_role_key UNIQUE (role);


--
-- TOC entry 2768 (class 2606 OID 64287)
-- Dependencies: 171 2762 169
-- Name: fk_app_role_id; Type: FK CONSTRAINT; Schema: public; Owner: artsholland
--

ALTER TABLE ONLY app
    ADD CONSTRAINT fk_app_role_id FOREIGN KEY (role_id) REFERENCES role(id);


--
-- TOC entry 2769 (class 2606 OID 64292)
-- Dependencies: 2760 171 167
-- Name: fk_app_user_id; Type: FK CONSTRAINT; Schema: public; Owner: artsholland
--

ALTER TABLE ONLY app
    ADD CONSTRAINT fk_app_user_id FOREIGN KEY (apiuser_id) REFERENCES apiuser(id);


-- Completed on 2012-08-16 14:58:37 CEST

--
-- PostgreSQL database dump complete
--


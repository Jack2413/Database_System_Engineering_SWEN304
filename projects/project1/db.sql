--
-- PostgreSQL database dump
--

-- Dumped from database version 10.3
-- Dumped by pg_dump version 10.3

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: cities; Type: TABLE; Schema: public; Owner: jackgan
--

CREATE TABLE public.cities (
    name character varying(80),
    location point
);


ALTER TABLE public.cities OWNER TO jackgan;

--
-- Name: weather; Type: TABLE; Schema: public; Owner: jackgan
--

CREATE TABLE public.weather (
    city character varying(80),
    temp_lo integer,
    temp_hi integer,
    prcp real,
    date date
);


ALTER TABLE public.weather OWNER TO jackgan;

--
-- Data for Name: cities; Type: TABLE DATA; Schema: public; Owner: jackgan
--

COPY public.cities (name, location) FROM stdin;
San Francisco	(-194,53)
\.


--
-- Data for Name: weather; Type: TABLE DATA; Schema: public; Owner: jackgan
--

COPY public.weather (city, temp_lo, temp_hi, prcp, date) FROM stdin;
San Francisco	46	50	0.25	1994-11-27
San Francisco	43	57	0	1994-11-29
San Francisco	43	57	0	1994-11-29
Hayward	37	54	\N	1994-11-29
\.


--
-- PostgreSQL database dump complete
--


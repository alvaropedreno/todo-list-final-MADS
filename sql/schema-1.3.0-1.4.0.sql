
--
-- Name: comentarios; Type: TABLE; Schema: public; Owner: mads
--
CREATE TABLE public.comentarios (
                                    id bigint NOT NULL,
                                    comentario character varying(255),
                                    fecha timestamp without time zone,
                                    tarea_id bigint,
                                    usuario_id bigint
);
ALTER TABLE public.comentarios OWNER TO mads;
--
-- Name: comentarios_id_seq; Type: SEQUENCE; Schema: public; Owner: mads
--
CREATE SEQUENCE public.comentarios_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER TABLE public.comentarios_id_seq OWNER TO mads;
--
-- Name: comentarios_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: mads
--
ALTER SEQUENCE public.comentarios_id_seq OWNED BY public.comentarios.id;

-- Agregar la columna 'deadline' de tipo timestamp
ALTER TABLE public.tareas
    ADD COLUMN deadline timestamp without time zone;

-- Agregar la columna 'estado' de tipo character varying(255)
ALTER TABLE public.tareas
    ADD COLUMN estado character varying(255);

-- Agregar la columna 'prioridad' de tipo character varying(255)
ALTER TABLE public.tareas
    ADD COLUMN prioridad character varying(255);

-- Agregar la columna 'tarea_padre_id' de tipo bigint
ALTER TABLE public.tareas
    ADD COLUMN tarea_padre_id bigint;

-- Agregar la columna 'foto' de tipo oid
ALTER TABLE public.usuarios
    ADD COLUMN foto oid;

-- Name: comentarios id; Type: DEFAULT; Schema: public; Owner: mads
--
ALTER TABLE ONLY public.comentarios ALTER COLUMN id SET DEFAULT nextval('public.comentarios_id_seq'::regclass);

--
-- Name: comentarios comentarios_pkey; Type: CONSTRAINT; Schema: public; Owner: mads
--
ALTER TABLE ONLY public.comentarios
    ADD CONSTRAINT comentarios_pkey PRIMARY KEY (id);

--
-- Name: comentarios fkdts62yj83qe3k748cgcjvm48r; Type: FK CONSTRAINT; Schema: public; Owner: mads
--
ALTER TABLE ONLY public.comentarios
    ADD CONSTRAINT fkdts62yj83qe3k748cgcjvm48r FOREIGN KEY (usuario_id) REFERENCES public.usuarios(id);
--


--
-- Name: tareas fko3mm06ltekbxsqqgw01ry84mb; Type: FK CONSTRAINT; Schema: public; Owner: mads
--
ALTER TABLE ONLY public.tareas
    ADD CONSTRAINT fko3mm06ltekbxsqqgw01ry84mb FOREIGN KEY (tarea_padre_id) REFERENCES public.tareas(id);
--
-- Name: comentarios fkoaibm9jt86k8b8ymcmrncatfx; Type: FK CONSTRAINT; Schema: public; Owner: mads
--
ALTER TABLE ONLY public.comentarios
    ADD CONSTRAINT fkoaibm9jt86k8b8ymcmrncatfx FOREIGN KEY (tarea_id) REFERENCES public.tareas(id);
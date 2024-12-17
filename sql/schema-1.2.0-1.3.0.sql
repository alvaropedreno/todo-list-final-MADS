-- Agregar la columna 'descripcion' de tipo character varying(255)
ALTER TABLE public.tareas
    ADD COLUMN descripcion character varying(255);
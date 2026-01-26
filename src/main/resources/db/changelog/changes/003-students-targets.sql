CREATE TABLE IF NOT EXISTS public.target_students
(
    target_id  bigint NOT NULL,
    student_id bigint NOT NULL,
    created_at timestamp without time zone NOT NULL DEFAULT now(),

    CONSTRAINT target_students_pkey PRIMARY KEY (target_id, student_id),

    CONSTRAINT target_students_target_id_fkey
        FOREIGN KEY (target_id)
        REFERENCES public.targets (id)
        ON DELETE CASCADE,

    CONSTRAINT target_students_student_id_fkey
        FOREIGN KEY (student_id)
        REFERENCES public.students (id)
        ON DELETE CASCADE
);

ALTER TABLE public.target_students OWNER TO rada_user;

CREATE INDEX IF NOT EXISTS idx_target_students_student_id
    ON public.target_students (student_id);

CREATE INDEX IF NOT EXISTS idx_target_students_target_id
    ON public.target_students (target_id);

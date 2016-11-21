ALTER TABLE item ADD COLUMN fts tsvector;
UPDATE item SET fts=
setweight( coalesce( to_tsvector('russian', name),''),'A') || ' ' ||
setweight( coalesce( to_tsvector('russian', description),''),'B');
create index fts_index on item using gin (fts);
CREATE FUNCTION item_vector_update() RETURNS TRIGGER AS $$
BEGIN
	IF (TG_OP = 'UPDATE') THEN
		IF ( OLD.name <> NEW.name or OLD.description <> NEW.description) THEN
			NEW.fts=setweight( coalesce( to_tsvector('russian', NEW.name),''),'A') || ' ' ||
			setweight( coalesce( to_tsvector('russian', NEW.description),''),'B');
			RETURN NEW;
		ELSE
			RETURN NEW;
		END IF;
	ELSIF (TG_OP = 'INSERT') THEN
        NEW.fts=setweight( coalesce( to_tsvector('russian', NEW.name),''),'A') || ' ' ||
			setweight( coalesce( to_tsvector('russian', NEW.description),''),'B');
			RETURN NEW;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE 'plpgsql';

CREATE TRIGGER item_fts_update BEFORE INSERT OR UPDATE ON item
FOR EACH ROW EXECUTE PROCEDURE item_vector_update();

update item
SET fts=
setweight( coalesce( to_tsvector('russian', name),''),'A') || ' ' ||
setweight( coalesce( to_tsvector('russian', description),''),'B');

-- Migration 624: Add rubella vaccination and congenital rubella fields to maternalhistory
-- This migration adds the new rubella-related fields to the maternalhistory table

-- Add new columns to maternalhistory table
ALTER TABLE maternalhistory ADD COLUMN IF NOT EXISTS rubellavaccination varchar(255);
ALTER TABLE maternalhistory ADD COLUMN IF NOT EXISTS rubellavaccinationdate timestamp;
ALTER TABLE maternalhistory ADD COLUMN IF NOT EXISTS rubellamonth integer;
ALTER TABLE maternalhistory ADD COLUMN IF NOT EXISTS congenitalrubella varchar(255);
ALTER TABLE maternalhistory ADD COLUMN IF NOT EXISTS congenitalrubelladate timestamp;

-- Add new columns to maternalhistory_history table
ALTER TABLE maternalhistory_history ADD COLUMN IF NOT EXISTS rubellavaccination varchar(255);
ALTER TABLE maternalhistory_history ADD COLUMN IF NOT EXISTS rubellavaccinationdate timestamp;
ALTER TABLE maternalhistory_history ADD COLUMN IF NOT EXISTS rubellamonth integer;
ALTER TABLE maternalhistory_history ADD COLUMN IF NOT EXISTS congenitalrubella varchar(255);
ALTER TABLE maternalhistory_history ADD COLUMN IF NOT EXISTS congenitalrubelladate timestamp;

-- Update schema version
INSERT INTO schema_version (version_number, comment) 
VALUES (624, 'Add rubella vaccination and congenital rubella fields to maternalhistory')
ON CONFLICT (version_number) DO NOTHING;

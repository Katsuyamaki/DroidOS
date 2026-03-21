-- scripts/clean_dict_with_nvim.lua

-- Configuration
local input_file = "dictionary.txt"
local output_file = "dictionary.txt" -- Overwrite strict or use new name
local backup_file = "dictionary.bak"

-- Setup Spelling
vim.opt.spell = true
vim.opt.spelllang = "en"

print("Starting dictionary sanitization...")

-- Read Input
local infile = io.open(input_file, "r")
if not infile then
	print("Error: Could not open " .. input_file)
	vim.cmd("q")
	return
end

local valid_words = {}
local count_total = 0
local count_kept = 0

for line in infile:lines() do
	local word = line:match("^%s*(.-)%s*$") -- Trim whitespace
	if word and word ~= "" then
		count_total = count_total + 1

		-- Check word against nvim dictionary
		-- spellbadword returns a list: [bad_word, error_type]
		-- error_type can be: 'bad' (unknown), 'caps', 'rare', 'local', etc.
		-- We only reject 'bad'. We accept 'caps' because swipe dicts are often lowercase.
		local result = vim.fn.spellbadword(word)
		local error_type = result[2]

		if error_type ~= "bad" then
			table.insert(valid_words, word)
			count_kept = count_kept + 1
		end
	end
end
infile:close()

-- Write Output
print("Sanitization complete.")
print("Total words scanned: " .. count_total)
print("Words kept: " .. count_kept)
print("Words removed: " .. (count_total - count_kept))

-- Backup original
os.rename(input_file, backup_file)
print("Original dictionary backed up to " .. backup_file)

local outfile = io.open(output_file, "w")
if outfile then
	for _, word in ipairs(valid_words) do
		outfile:write(word .. "\n")
	end
	outfile:close()
	print("Clean dictionary saved to " .. output_file)
else
	print("Error: Could not write to " .. output_file)
end

vim.cmd("q")

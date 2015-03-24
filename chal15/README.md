# chal15

Solution for challenge 13: Will output the flag in the terminal.

Do not run `make`. It will be ran by the Clojure program.
Must be ran on a 64 bit Linux machine.

Not necessary but for educative purpose:
It gets a temp dir to place library and executable.
It first builds a .so that contains a dummy sleep function.
It gets the challenge from R0T.
It base64 decodes until the last byte is 0x7F (the first byte of an ELF)
It does a LD_PRELOAD with our previously build .so so we don't have to wait to get the flag.
It prints the flag.

## Usage
    
    $ lein run <YOUR-PHPSESSID>


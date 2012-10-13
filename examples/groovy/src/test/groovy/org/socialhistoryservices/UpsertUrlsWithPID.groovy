package org.socialhistoryservices

class UpsertUrlsWithPIDRun {

    public static void main(String[] args) {
        try {
            UpsertUrlsWithPID.main(['-file', 'a file', '-wskey', 'a key', '-na', 'a naming authority'])  // omit endpoint
        } catch (Exception e) {
            assert e.message.contains('missing')
        }
    }
}

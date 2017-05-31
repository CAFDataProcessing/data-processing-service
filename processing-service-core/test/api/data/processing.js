/*
 * Copyright 2015-2017 Hewlett Packard Enterprise Development LP.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
//example Policy API call responses to use as test data for processing

module.exports = {};
module.exports.rules = {
  "totalHits": 6,
  "rules": [
    {
      "id": 1,
      "name": "My new sequence 1",
      "description": "A description for the new sequence 1.",
      "priority": 0
    },
    {
      "id": 2,
      "name": "My new sequence 2",
      "description": "A description for the new sequence 2.",
      "priority": 1
    },
    {
      "id": 3,
      "name": "My new sequence 3",
      "description": "A description for the new sequence 3.",
      "priority": 2
    },
    {
      "id": 4,
      "name": "My new sequence 4",
      "description": "A description for the new sequence 4.",
      "priority": 3
    },
    {
      "id": 5,
      "name": "My new sequence 5",
      "description": "A description for the new sequence 5.",
      "priority": 4
    },
    {
      "id": 6,
      "name": "My new sequence 6",
      "description": "A description for the new sequence 6.",
      "priority": 5
    }
  ]
};

module.exports.workflows = {
  "1": {
    "results": [{
      "type": "sequence_workflow",
      "id": 1,
      "name": "Testing Gateway",
      "description": "",
      "additional": {
        "sequence_entries": [{
          "collection_sequence_id": 1,
          "order": 0,
          "sequence_workflow_id": 1,
          "collection_sequence": null
        },
        {
          "collection_sequence_id": 2,
          "order": 1,
          "sequence_workflow_id": 1,
          "collection_sequence": null
        },
        {
          "collection_sequence_id": 3,
          "order": 2,
          "sequence_workflow_id": 1,
          "collection_sequence": null
        },
        {
          "collection_sequence_id": 4,
          "order": 3,
          "sequence_workflow_id": 1,
          "collection_sequence": null
        },
        {
          "collection_sequence_id": 5,
          "order": 4,
          "sequence_workflow_id": 1,
          "collection_sequence": null
        },
        {
          "collection_sequence_id": 6,
          "order": 5,
          "sequence_workflow_id": 1,
          "collection_sequence": null
        },],
        "notes": ""
      }
    }],
    "totalhits": 1
  }
};

module.exports.workflowEntries  = {
	"results": [{
		"type": "sequence_workflow_entry",
		"id": null,
		"additional": {
			"collection_sequence_id": 1,
			"order": 0,
			"collection_sequence": null,
			"sequence_workflow_id": 1
		}
	},
	{
		"type": "sequence_workflow_entry",
		"id": null,
		"additional": {
			"collection_sequence_id": 2,
			"order": 1,
			"collection_sequence": null,
			"sequence_workflow_id": 1
		}
	},
	{
		"type": "sequence_workflow_entry",
		"id": null,
		"additional": {
			"collection_sequence_id": 3,
			"order": 2,
			"collection_sequence": null,
			"sequence_workflow_id": 1
		}
	},
	{
		"type": "sequence_workflow_entry",
		"id": null,
		"additional": {
			"collection_sequence_id": 4,
			"order": 3,
			"collection_sequence": null,
			"sequence_workflow_id": 1
		}
	},
	{
		"type": "sequence_workflow_entry",
		"id": null,
		"additional": {
			"collection_sequence_id": 5,
			"order": 4,
			"collection_sequence": null,
			"sequence_workflow_id": 1
		}
	},
	{
		"type": "sequence_workflow_entry",
		"id": null,
		"additional": {
			"collection_sequence_id": 6,
			"order": 5,
			"collection_sequence": null,
			"sequence_workflow_id": 1
		}
	}],
	"totalhits": 6
};

module.exports.collectionSequences = {
  "1":
  {
    "results": [{
      "type": "collection_sequence",
      "id": 1,
      "name": "My new sequence 1",
      "description": "A description for the new sequence 1.",
      "additional": {
        "collection_sequence_entries": [],
        "default_collection_id": null,
        "collection_count": 0,
        "last_modified": "2016-07-15T05:55:55.504Z",
        "full_condition_evaluation": false,
        "metrics_enabled": false,
        "evaluation_enabled": true,
        "excluded_document_condition_id": null,
        "fingerprint": null
      }
    }],
    "totalhits": 1
  },
  "2":
  {
    "results": [{
      "type": "collection_sequence",
      "id": 2,
      "name": "My new sequence 2",
      "description": "A description for the new sequence 2.",
      "additional": {
        "collection_sequence_entries": [],
        "default_collection_id": null,
        "collection_count": 0,
        "last_modified": "2016-07-15T05:55:55.504Z",
        "full_condition_evaluation": false,
        "metrics_enabled": false,
        "evaluation_enabled": true,
        "excluded_document_condition_id": null,
        "fingerprint": null
      }
    }],
    "totalhits": 1
  },
  "3":
  {
    "results": [{
      "type": "collection_sequence",
      "id": 3,
      "name": "My new sequence 3",
      "description": "A description for the new sequence 3.",
      "additional": {
        "collection_sequence_entries": [],
        "default_collection_id": null,
        "collection_count": 0,
        "last_modified": "2016-07-15T05:55:55.504Z",
        "full_condition_evaluation": false,
        "metrics_enabled": false,
        "evaluation_enabled": true,
        "excluded_document_condition_id": null,
        "fingerprint": null
      }
    }],
    "totalhits": 1
  },
  "4":
  {
    "results": [{
      "type": "collection_sequence",
      "id": 4,
      "name": "My new sequence 4",
      "description": "A description for the new sequence 4.",
      "additional": {
        "collection_sequence_entries": [],
        "default_collection_id": null,
        "collection_count": 0,
        "last_modified": "2016-07-15T05:55:55.504Z",
        "full_condition_evaluation": false,
        "metrics_enabled": false,
        "evaluation_enabled": true,
        "excluded_document_condition_id": null,
        "fingerprint": null
      }
    }],
    "totalhits": 1
  },
  "5":
  {
    "results": [{
      "type": "collection_sequence",
      "id": 5,
      "name": "My new sequence 5",
      "description": "A description for the new sequence 5.",
      "additional": {
        "collection_sequence_entries": [],
        "default_collection_id": null,
        "collection_count": 0,
        "last_modified": "2016-07-15T05:55:55.504Z",
        "full_condition_evaluation": false,
        "metrics_enabled": false,
        "evaluation_enabled": true,
        "excluded_document_condition_id": null,
        "fingerprint": null
      }
    }],
    "totalhits": 1
  },
  "6":
  {
    "results": [{
      "type": "collection_sequence",
      "id": 6,
      "name": "My new sequence 6",
      "description": "A description for the new sequence 6.",
      "additional": {
        "collection_sequence_entries": [],
        "default_collection_id": null,
        "collection_count": 0,
        "last_modified": "2016-07-15T05:55:55.504Z",
        "full_condition_evaluation": false,
        "metrics_enabled": false,
        "evaluation_enabled": true,
        "excluded_document_condition_id": null,
        "fingerprint": null
      }
    }],
    "totalhits": 1
  }
};
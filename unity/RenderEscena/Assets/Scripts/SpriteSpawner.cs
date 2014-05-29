using UnityEngine;
using System.Collections;

public class SpriteSpawner : MonoBehaviour {

	public GameObject team1;
	public GameObject team2;
	public GameObject teamNeutral;

	float getRandomX() {
		return ((UnityEngine.Random.value + UnityEngine.Random.value) * 45f) - 45f;
	}

	float getRandomZ() {
		return ((UnityEngine.Random.value + UnityEngine.Random.value) * 22.5f) - 22.5f;
	}

	// Use this for initialization
	void Start () {
		float a, b;

		for (int i = 0; i < 11; i++) {
			a = getRandomX();
			b = getRandomZ();
			Instantiate(team1, new Vector3(a, 1.5f, b), Quaternion.identity);
			a = getRandomX();
			b = getRandomZ();
			Instantiate(team2, new Vector3(a, 1.5f, b), Quaternion.identity);
		}
		a = getRandomX();
		b = getRandomZ();
		
		Instantiate(teamNeutral, new Vector3(a, 1.5f, b), Quaternion.identity);
	}
}
